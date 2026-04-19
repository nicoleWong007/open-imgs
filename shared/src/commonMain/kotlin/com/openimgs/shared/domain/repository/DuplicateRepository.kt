package com.openimgs.shared.domain.repository

import com.openimgs.shared.domain.model.DuplicateGroup
import com.openimgs.shared.domain.model.DuplicateStatus
import com.openimgs.shared.domain.model.Photo

interface DuplicateRepository {
    suspend fun getAllGroups(): List<DuplicateGroup>
    suspend fun getGroupsByStatus(status: DuplicateStatus): List<DuplicateGroup>
    suspend fun getPhotosInGroup(groupId: String): List<Photo>
    suspend fun insertGroup(group: DuplicateGroup)
    suspend fun addDuplicatePhoto(groupId: String, photoId: String)
    suspend fun updateGroupStatus(groupId: String, status: DuplicateStatus, reviewedAt: Long)
    suspend fun deleteGroup(groupId: String)
    suspend fun getPendingCount(): Int
}
