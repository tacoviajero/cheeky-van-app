# Dot-source this helper when using the ignored, project-local validation tools.
# Usage: . .\scripts\Use-LocalToolchain.ps1
$machineDashboardRoot = Split-Path -Parent $PSScriptRoot
$machineDashboardJava = Get-ChildItem -LiteralPath (Join-Path $machineDashboardRoot '.tools\java') -Directory -ErrorAction Stop | Select-Object -First 1
$machineDashboardSdk = Join-Path $machineDashboardRoot '.tools\android-sdk'
if (-not (Test-Path -LiteralPath (Join-Path $machineDashboardSdk 'platforms\android-36\android.jar'))) {
    throw 'Project-local Android SDK 36 is missing. Configure an installed SDK using the README.'
}
$env:JAVA_HOME = $machineDashboardJava.FullName
$env:ANDROID_HOME = $machineDashboardSdk
$env:ANDROID_USER_HOME = Join-Path $machineDashboardRoot '.tools\android-user'
$env:GRADLE_USER_HOME = Join-Path $machineDashboardRoot '.tools\gradle-home'
$env:Path = "$env:JAVA_HOME\bin;$machineDashboardSdk\platform-tools;$env:Path"
