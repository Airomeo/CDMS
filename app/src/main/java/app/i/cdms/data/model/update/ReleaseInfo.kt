package app.i.cdms.data.model.update


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleaseInfo(
    @Json(name = "android_min_api_level")
    val androidMinApiLevel: String,
    @Json(name = "app_display_name")
    val appDisplayName: String,
    @Json(name = "app_name")
    val appName: String,
    @Json(name = "app_os")
    val appOs: String,
    @Json(name = "bundle_identifier")
    val bundleIdentifier: String,
    @Json(name = "can_resign")
    val canResign: Any?,
    @Json(name = "destination_type")
    val destinationType: String,
    @Json(name = "device_family")
    val deviceFamily: Any?,
    @Json(name = "distribution_group_id")
    val distributionGroupId: String,
    @Json(name = "distribution_groups")
    val distributionGroups: List<DistributionGroup>,
    @Json(name = "download_url")
    val downloadUrl: String,
    @Json(name = "enabled")
    val enabled: Boolean,
    @Json(name = "fileExtension")
    val fileExtension: String,
    @Json(name = "fingerprint")
    val fingerprint: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "install_url")
    val installUrl: String,
    @Json(name = "is_external_build")
    val isExternalBuild: Boolean,
    @Json(name = "is_latest")
    val isLatest: Boolean,
    @Json(name = "is_udid_provisioned")
    val isUdidProvisioned: Any?,
    @Json(name = "mandatory_update")
    val mandatoryUpdate: Boolean,
    @Json(name = "min_os")
    val minOs: String,
    @Json(name = "origin")
    val origin: String,
    @Json(name = "owner")
    val owner: Owner,
    @Json(name = "package_hashes")
    val packageHashes: List<String>,
    @Json(name = "release_notes")
    val releaseNotes: String,
    @Json(name = "short_version")
    val shortVersion: String,
    @Json(name = "size")
    val size: Int,
    @Json(name = "status")
    val status: String,
    @Json(name = "uploaded_at")
    val uploadedAt: String,
    @Json(name = "version")
    val version: String
)