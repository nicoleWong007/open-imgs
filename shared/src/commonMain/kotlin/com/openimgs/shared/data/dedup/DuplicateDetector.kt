package com.openimgs.shared.data.dedup

import com.openimgs.shared.data.repository.SqlDelightPhotoRepository
import com.openimgs.shared.domain.model.DuplicateGroup
import com.openimgs.shared.domain.repository.DuplicateRepository
import com.openimgs.shared.domain.repository.PhotoRepository

class DuplicateDetector(
    private val photoRepository: PhotoRepository,
    private val duplicateRepository: DuplicateRepository,
    private val hasher: ImageHasher = PerceptualHasher(),
    private val similarityThreshold: Float = 0.9f
) {

    data class HashEntry(
        val photoId: String,
        val phash: Long,
        val dhash: Long
    )

    suspend fun computeHashesForUnindexed(batchSize: Int = 100): Int {
        val unindexedIds = photoRepository.getUnindexedPhotoIds(batchSize)
        for (id in unindexedIds) {
            val photo = photoRepository.getPhotoById(id) ?: continue
            val (phash, dhash) = computeHashForPhoto(photo.path)
            photoRepository.updateHashes(id, phash.toString(16), dhash.toString(16))
        }
        return unindexedIds.size
    }

    suspend fun findDuplicates(hashEntries: List<HashEntry>): List<DuplicateGroup> {
        val groups = mutableListOf<DuplicateGroup>()
        val processed = mutableSetOf<String>()

        for (i in hashEntries.indices) {
            val entry1 = hashEntries[i]
            if (entry1.photoId in processed) continue

            val duplicates = mutableListOf<HashEntry>()

            for (j in (i + 1) until hashEntries.indices.count()) {
                val entry2 = hashEntries[j]
                if (entry2.photoId in processed) continue

                val phashSim = hasher.similarity(entry1.phash, entry2.phash)
                val dhashSim = hasher.similarity(entry1.dhash, entry2.dhash)
                val combinedSim = 0.6f * phashSim + 0.4f * dhashSim

                if (combinedSim >= similarityThreshold) {
                    duplicates.add(entry2)
                    processed.add(entry2.photoId)
                }
            }

            if (duplicates.isNotEmpty()) {
                val group = DuplicateGroup(
                    id = generateGroupId(entry1.photoId),
                    originalId = entry1.photoId,
                    duplicateIds = duplicates.map { it.photoId },
                    similarity = duplicates.maxOf { d ->
                        hasher.similarity(entry1.phash, d.phash)
                    }
                )
                groups.add(group)
                duplicateRepository.insertGroup(group)
                processed.add(entry1.photoId)
            }
        }

        return groups
    }

    private fun computeHashForPhoto(path: String): Pair<Long, Long> {
        // Platform layer will provide pixel data; for now return placeholder
        // Actual pixel loading happens via ThumbnailLoader → pixels → hasher
        return Pair(0L, 0L)
    }

    private fun generateGroupId(originalId: String): String {
        return "dup_${originalId.take(16)}_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
    }
}
