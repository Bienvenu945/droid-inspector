package com.droidinspector.domain.repository

import android.net.Uri
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.domain.model.BatteryInfo
import com.droidinspector.domain.model.DeviceSummary
import com.droidinspector.domain.model.HardwareInfo
import com.droidinspector.domain.model.SafFileItem
import com.droidinspector.domain.model.SensorInfo
import com.droidinspector.domain.model.StorageInfo
import com.droidinspector.domain.model.SystemSecurityInfo
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceSummary(): DeviceSummary
    fun getSystemSecurityInfo(): SystemSecurityInfo
    fun getBatteryInfo(): Flow<BatteryInfo>
    fun getCurrentBatterySnapshot(): BatteryInfo
    suspend fun getHardwareInfo(): HardwareInfo
    fun getAvailableSensors(): List<SensorInfo>
    fun getSensorUpdates(sensorType: Int): Flow<List<Float>>
    suspend fun getStorageInfo(): StorageInfo
    suspend fun listSafDirectory(treeUri: Uri, documentId: String? = null): List<SafFileItem>
    suspend fun getInstalledApplications(): List<AppPackageInfo>
}
