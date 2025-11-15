package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.StudyMaterial
import com.example.hulaba3.data.database.StudyMaterialDao

class StudyMaterialRepository(
    private val studyMaterialDao: StudyMaterialDao
) {
    suspend fun insertMaterial(material: StudyMaterial): Long =
        studyMaterialDao.insertMaterial(material)

    suspend fun clearPrimaryMaterials(topicId: String) =
        studyMaterialDao.clearPrimaryMaterials(topicId)

    suspend fun setAsPrimary(materialId: Long) =
        studyMaterialDao.setAsPrimary(materialId)

    suspend fun getPrimaryMaterialByTopic(topicId: String): StudyMaterial? =
        studyMaterialDao.getPrimaryMaterialByTopic(topicId)
}