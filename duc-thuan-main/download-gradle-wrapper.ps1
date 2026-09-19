# Download Gradle Wrapper JAR
# Chạy script này từ thư mục gốc dự án: .\download-gradle-wrapper.ps1
# Yêu cầu: PowerShell 5.0+

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$WrapperDir = Join-Path $ProjectRoot "gradle" "wrapper"
$JarPath = Join-Path $WrapperDir "gradle-wrapper.jar"
$PropertiesPath = Join-Path $WrapperDir "gradle-wrapper.properties"

# Đọc phiên bản Gradle từ gradle-wrapper.properties
if (Test-Path $PropertiesPath) {
    $Properties = Get-Content $PropertiesPath -Raw
    if ($Properties -match 'distributionUrl=.*?gradle-([\d.]+)-') {
        $GradleVersion = $Matches[1]
        Write-Host "Detected Gradle version: $GradleVersion" -ForegroundColor Cyan
    } else {
        Write-Host "Could not parse Gradle version from gradle-wrapper.properties, using default 9.3.1" -ForegroundColor Yellow
        $GradleVersion = "9.3.1"
    }
} else {
    Write-Host "gradle-wrapper.properties not found. Using default version 9.3.1" -ForegroundColor Yellow
    $GradleVersion = "9.3.1"
}

# Tạo thư mục nếu chưa có
if (-not (Test-Path $WrapperDir)) {
    New-Item -ItemType Directory -Path $WrapperDir -Force | Out-Null
    Write-Host "Created directory: $WrapperDir" -ForegroundColor Green
}

# URL tải gradle-wrapper.jar từ GitHub (phiên bản chính xác)
$JarUrl = "https://github.com/gradle/gradle/raw/v$GradleVersion/gradle/wrapper/gradle-wrapper.jar"

Write-Host ""
Write-Host "Downloading gradle-wrapper.jar..." -ForegroundColor Cyan
Write-Host "  From: $JarUrl" -ForegroundColor Gray
Write-Host "  To:   $JarPath" -ForegroundColor Gray
Write-Host ""

try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $WebClient = New-Object System.Net.WebClient
    $WebClient.DownloadFile($JarUrl, $JarPath)

    # Verify the downloaded file
    if (Test-Path $JarPath) {
        $FileSize = (Get-Item $JarPath).Length
        if ($FileSize -gt 50000) {
            Write-Host "Download complete! File size: $([math]::Round($FileSize / 1KB, 1)) KB" -ForegroundColor Green
        } else {
            Write-Host "WARNING: File seems too small ($FileSize bytes). The JAR may be corrupted." -ForegroundColor Yellow
            Write-Host "Trying alternative download source..." -ForegroundColor Yellow

            # Fallback: download from Gradle distribution ZIP
            $ZipUrl = "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip"
            $TempZip = Join-Path $env:TEMP "gradle-$GradleVersion-bin.zip"

            Write-Host "  Downloading Gradle distribution..." -ForegroundColor Cyan
            $WebClient.DownloadFile($ZipUrl, $TempZip)

            # Extract JAR from ZIP (requires .NET 5+ or PowerShell 7+)
            Add-Type -AssemblyName System.IO.Compression.FileSystem
            Write-Host "  Extracting gradle-wrapper.jar from ZIP..." -ForegroundColor Cyan
            using ($Zip = [System.IO.Compression.ZipFile]::OpenRead($TempZip)) {
                $Entry = $Zip.Entries | Where-Object { $_.Name -eq "gradle-wrapper.jar" }
                if ($Entry) {
                    [System.IO.Compression.ZipFileExtensions]::ExtractToFile($Entry, $JarPath, $true)
                    Write-Host "  Extracted successfully!" -ForegroundColor Green
                } else {
                    Write-Host "  Could not find gradle-wrapper.jar inside the ZIP." -ForegroundColor Red
                    throw "JAR not found in distribution ZIP"
                }
            }
            Remove-Item $TempZip -Force -ErrorAction SilentlyContinue
        }

        Write-Host ""
        Write-Host "Setup complete! You can now run:" -ForegroundColor Green
        Write-Host "  .\gradlew.bat build" -ForegroundColor White
        Write-Host ""
    } else {
        throw "Download failed: file not created"
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Failed to download gradle-wrapper.jar" -ForegroundColor Red
    Write-Host "  $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual instructions:" -ForegroundColor Yellow
    Write-Host "  1. Download Gradle $GradleVersion from https://services.gradle.org/distributions/" -ForegroundColor Gray
    Write-Host "  2. Extract the ZIP and find 'gradle/wrapper/gradle-wrapper.jar'" -ForegroundColor Gray
    Write-Host "  3. Copy it to: $JarPath" -ForegroundColor Gray
    exit 1
}
