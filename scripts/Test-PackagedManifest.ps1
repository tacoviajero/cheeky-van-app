param(
    [string]$ManifestPath = (Join-Path $PSScriptRoot '../app/build/intermediates/packaged_manifests/debug/processDebugManifestForPackage/AndroidManifest.xml')
)

$ErrorActionPreference = 'Stop'
[xml]$machineDashboardManifest = Get-Content -LiteralPath $ManifestPath -Raw
$machineDashboardNamespace = 'http://schemas.android.com/apk/res/android'
$machineDashboardInternalPermission = 'com.machinedashboard.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION'
$cheekyVanAllowedPermissions = @{
    'com.machinedashboard.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION' = ''
    'android.permission.BLUETOOTH' = '30'
    'android.permission.BLUETOOTH_CONNECT' = ''
    'android.permission.ACCESS_COARSE_LOCATION' = ''
    'android.permission.ACCESS_FINE_LOCATION' = ''
}

$machineDashboardPermissions = $machineDashboardManifest.SelectNodes('/manifest/uses-permission | /manifest/uses-permission-sdk-23')
$machineDashboardDeclarations = $machineDashboardManifest.SelectNodes('/manifest/permission')
if ($machineDashboardPermissions.Count -gt 0 -and $machineDashboardDeclarations.Count -ne 1) {
    throw 'The AndroidX internal permission must have one signature-protected declaration.'
}
foreach ($machineDashboardPermission in $machineDashboardPermissions) {
    $machineDashboardPermissionName = $machineDashboardPermission.GetAttribute('name', $machineDashboardNamespace)
    if (-not $cheekyVanAllowedPermissions.ContainsKey($machineDashboardPermissionName)) {
        throw "Unexpected packaged permission: $machineDashboardPermissionName"
    }
    if ($machineDashboardPermission.GetAttribute('maxSdkVersion', $machineDashboardNamespace) -ne $cheekyVanAllowedPermissions[$machineDashboardPermissionName]) {
        throw "Unexpected SDK scope for permission: $machineDashboardPermissionName"
    }
}

foreach ($machineDashboardDeclaration in $machineDashboardDeclarations) {
    if ($machineDashboardDeclaration.GetAttribute('name', $machineDashboardNamespace) -ne $machineDashboardInternalPermission -or
        $machineDashboardDeclaration.GetAttribute('protectionLevel', $machineDashboardNamespace) -ne 'signature') {
        throw 'Unexpected custom permission or protection level.'
    }
}

$machineDashboardApplication = $machineDashboardManifest.SelectSingleNode('/manifest/application')
if ($null -eq $machineDashboardApplication -or $machineDashboardApplication.GetAttribute('allowBackup', $machineDashboardNamespace) -ne 'false') {
    throw 'The packaged application must disable Android backup.'
}
if ($machineDashboardApplication.GetAttribute('fullBackupContent', $machineDashboardNamespace) -ne 'false' -or
    $machineDashboardApplication.GetAttribute('dataExtractionRules', $machineDashboardNamespace) -ne '@xml/data_extraction_rules') {
    throw 'The packaged application must use the verified backup and transfer exclusions.'
}

Write-Output 'Packaged manifest privacy check passed: only paired Bluetooth, foreground location and the AndroidX internal signature permission are allowed; Android backup is disabled.'
