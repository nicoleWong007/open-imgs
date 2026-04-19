package com.openimgs.shared.data.repository

import com.openimgs.shared.data.database.DatabaseHelper
import com.openimgs.shared.domain.model.DuplicateGroup
import com.openimgs.shared.domain.model.DuplicateStatus
import com.openimgs.shared.domain.model.Photo
import com.openimgs.shared.domain.repository.DuplicateRepository
import kotlinx.datetime.Instant

class SqlDelightDuplicateRepository(
    private val db: DatabaseHelper
) : DuplicateRepository {

    override suspend fun getAllGroups(): List<DuplicateGroup> {
        return db.duplicateQueries.selectAll()
            .executeAsList()
            .map { it.toDuplicateGroup() }
    }

    override suspend fun getGroupsByStatus(status: DuplicateStatus): List<DuplicateGroup> {
        return db.duplicateQueries.selectByStatus(status.name)
            .executeAsList()
            .map { it.toDuplicateGroup() }
    }

    override suspend fun getPhotosInGroup(groupId: String): List<Photo> {
        return db.duplicateQueries.selectPhotosByGroup(groupId)
            .executeAsList()
            .map { it.toPhoto() }
    }

    override suspend fun insertGroup(group: DuplicateGroup) {
        db.duplicateQueries.insertGroup(
            id = group.id,
            original_id = group.originalId,
            similarity = group.similarity.toDouble(),
            status = group.status.name,
            created_at = Instant.fromEpochMilliseconds(0).toEpochMilliseconds()
        )
        group.duplicateIds.forEach { photoId ->
            db.duplicateQueries.addDuplicatePhoto(group.id, photoId)
        }
    }

    override suspend fun addDuplicatePhoto(groupId: String, photoId: String) {
        db.duplicateQueries.addDuplicatePhoto(groupId, photoId)
    }

    override suspend fun updateGroupStatus(groupId: String, status: DuplicateStatus, reviewedAt: Long) {
        db.duplicateQueries.updateStatus(status.name, reviewedAt, groupId)
    }

    override suspend fun deleteGroup(groupId: String) {
        db.duplicateQueries.deleteGroup(groupId)
    }

    override suspend fun getPendingCount(): Int {
        return db.duplicateQueries.pendingCount()
            .executeAsOne()
            .toInt()
    }
}

private fun com.openimgs.shared.database.Duplicate_groups.toDuplicateGroup(): DuplicateGroup {
    return DuplicateGroup(
        id = id,
        originalId = original_id,
        duplicateIds = emptyList(),
        similarity = similarity.toFloat(),
        status = try {
            DuplicateStatus.valueOf(status)
        } catch (_: IllegalArgumentException) {
            DuplicateStatus.PENDING
        }
    )
}

private fun com.openimgs.shared.database.Photos.toPhoto(): Photo {
    return Photo(
        id = id,
        path = path,
        dateTaken = Instant.fromEpochMilliseconds(date_taken),
        size = size,
        mimeType = mime_type,
        width = width.toInt(),
        height = height.toInt(),
        orientation = orientation.toInt(),
        isFavorite = is_favorite == 1L
    )
}
