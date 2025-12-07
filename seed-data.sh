#!/bin/bash

# Methane Leak Detection - Seed Data Script
# Usage: ./seed-data.sh [BASE_URL]
# Example: ./seed-data.sh https://azercosmos-back-production.up.railway.app

BASE_URL="${1:-https://azercosmos-back-production.up.railway.app}"

echo "🌱 Seeding data to: $BASE_URL"
echo "=================================="

# Function to create leak
create_leak() {
    local data=$1
    echo "Creating leak..."
    curl -s -X POST "$BASE_URL/api/leaks" \
        -H "Content-Type: application/json" \
        -d "$data" | jq -r '.id + " - " + .locationName + " (" + .severity + ")"' 2>/dev/null || echo "Created"
}

# Run AI detection a few times
echo ""
echo "🤖 Running AI Detection (5 times)..."
for i in {1..5}; do
    result=$(curl -s -X POST "$BASE_URL/api/leaks/run-detection")
    detected=$(echo $result | jq -r '.leakDetected')
    if [ "$detected" = "true" ]; then
        id=$(echo $result | jq -r '.leak.id')
        location=$(echo $result | jq -r '.leak.locationName')
        echo "  ✅ Detected: $id - $location"
    else
        echo "  ⬜ No leak detected"
    fi
done

echo ""
echo "📝 Creating historical leaks..."

# High severity - Recent
create_leak '{
    "date": "2025-12-05",
    "locationName": "Neft Dashlari Platform",
    "severity": "HIGH",
    "status": "NEW",
    "detectedBy": "Sentinel-5P",
    "revenueLoss": 28500,
    "coordinates": [[40.5, 50.8], [40.5, 50.85], [40.45, 50.85], [40.45, 50.8]]
}'

# High severity - Dispatched
create_leak '{
    "date": "2025-12-03",
    "locationName": "Shah Deniz Field (Offshore)",
    "severity": "HIGH",
    "status": "DISPATCHED",
    "detectedBy": "GHGSat",
    "revenueLoss": 35000,
    "coordinates": [[40.1, 50.2], [40.1, 50.28], [40.02, 50.28], [40.02, 50.2]]
}'

# Medium severity - Verified
create_leak '{
    "date": "2025-12-01",
    "locationName": "Gunashli Field",
    "severity": "MEDIUM",
    "status": "VERIFIED",
    "detectedBy": "TROPOMI",
    "revenueLoss": 12500,
    "coordinates": [[40.2, 49.8], [40.2, 49.85], [40.15, 49.85], [40.15, 49.8]]
}'

# Low severity - New
create_leak '{
    "date": "2025-11-28",
    "locationName": "Sangachal Terminal",
    "severity": "LOW",
    "status": "NEW",
    "detectedBy": "MethaneSat",
    "revenueLoss": 4500,
    "coordinates": [[40.3, 49.5], [40.3, 49.55], [40.25, 49.55], [40.25, 49.5]]
}'

# Medium - Old, Verified
create_leak '{
    "date": "2025-11-20",
    "locationName": "Baku Oil Refinery Complex",
    "severity": "MEDIUM",
    "status": "VERIFIED",
    "detectedBy": "Sentinel-5P",
    "revenueLoss": 9800,
    "coordinates": [[40.4, 49.9], [40.4, 49.95], [40.35, 49.95], [40.35, 49.9]]
}'

# False positive example
create_leak '{
    "date": "2025-11-15",
    "locationName": "Heydar Aliyev Refinery",
    "severity": "HIGH",
    "status": "FALSE_POSITIVE",
    "detectedBy": "GHGSat",
    "revenueLoss": 0,
    "coordinates": [[40.6, 50.1], [40.6, 50.15], [40.55, 50.15], [40.55, 50.1]]
}'

# West Absheron - Dispatched
create_leak '{
    "date": "2025-11-10",
    "locationName": "West Absheron Gas Field",
    "severity": "MEDIUM",
    "status": "DISPATCHED",
    "detectedBy": "TROPOMI",
    "revenueLoss": 15200,
    "coordinates": [[39.8, 50.5], [39.8, 50.55], [39.75, 50.55], [39.75, 50.5]]
}'

# Chirag Field - Large polygon
create_leak '{
    "date": "2025-11-05",
    "locationName": "Chirag Field (Offshore)",
    "severity": "HIGH",
    "status": "VERIFIED",
    "detectedBy": "MethaneSat",
    "revenueLoss": 42000,
    "coordinates": [[40.0, 51.0], [40.0, 51.1], [39.9, 51.15], [39.8, 51.1], [39.8, 51.0], [39.9, 50.95]]
}'

echo ""
echo "=================================="
echo "✅ Seed data complete!"
echo ""
echo "📊 View all leaks:"
echo "   curl $BASE_URL/api/leaks"
echo ""
echo "🌐 Swagger UI:"
echo "   $BASE_URL/swagger-ui.html"
