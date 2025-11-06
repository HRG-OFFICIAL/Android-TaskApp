# Firebase Connection Test Script (PowerShell)
# This script helps test Firebase connectivity and populate sample data

Write-Host "🚀 Firebase Connection Test Script" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check if Firebase CLI is installed
try {
    $firebaseVersion = firebase --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Firebase CLI found: $firebaseVersion" -ForegroundColor Green
    } else {
        throw "Firebase CLI not found"
    }
} catch {
    Write-Host "❌ Firebase CLI not found. Please install it first:" -ForegroundColor Red
    Write-Host "   npm install -g firebase-tools" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# Check if user is logged in
try {
    firebase projects:list 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Firebase authentication verified" -ForegroundColor Green
    } else {
        throw "Not logged in"
    }
} catch {
    Write-Host "❌ Not logged in to Firebase. Please login first:" -ForegroundColor Red
    Write-Host "   firebase login" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# List available projects
Write-Host ""
Write-Host "📋 Available Firebase projects:" -ForegroundColor Blue
firebase projects:list

Write-Host ""
Write-Host "🔧 To use this test script with your TaskApp:" -ForegroundColor Magenta
Write-Host ""
Write-Host "1. Make sure your google-services.json is in the app/ directory" -ForegroundColor White
Write-Host "2. Build and run the app:" -ForegroundColor White
Write-Host "   .\gradlew :app:assembleDebug" -ForegroundColor Gray
Write-Host "   .\gradlew :app:installDebug" -ForegroundColor Gray
Write-Host ""
Write-Host "3. In the app, navigate to Settings > Firebase Test" -ForegroundColor White
Write-Host "   Or add the FirebaseTestScreen to your navigation" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Tap 'Run Firebase Test' to:" -ForegroundColor White
Write-Host "   ✅ Test Firestore connection" -ForegroundColor Green
Write-Host "   ✅ Create 3 demo users (Premium, Free, Guest)" -ForegroundColor Green
Write-Host "   ✅ Populate colorful sample tasks" -ForegroundColor Green
Write-Host "   ✅ Verify data retrieval" -ForegroundColor Green
Write-Host ""
Write-Host "5. The sample data will showcase:" -ForegroundColor White
Write-Host "   🎨 8 different task category colors" -ForegroundColor Yellow
Write-Host "   ⭐ Important task indicators" -ForegroundColor Yellow
Write-Host "   📊 Progress tracking (0-100%%)" -ForegroundColor Yellow
Write-Host "   ✅ Completed vs pending tasks" -ForegroundColor Yellow
Write-Host "   🏷️ Task tags and priorities" -ForegroundColor Yellow
Write-Host ""
Write-Host "6. Use 'Cleanup Test Data' to remove sample data" -ForegroundColor White
Write-Host ""
Write-Host "🎯 This demonstrates the enhanced UI features:" -ForegroundColor Cyan
Write-Host "   • Colorful task cards with category backgrounds" -ForegroundColor White
Write-Host "   • Staggered grid layout" -ForegroundColor White
Write-Host "   • Search and filter functionality" -ForegroundColor White
Write-Host "   • Selection mode for bulk operations" -ForegroundColor White
Write-Host "   • Beautiful statistics with progress indicators" -ForegroundColor White
Write-Host ""
Write-Host "🔧 CLI seeding (Node + Admin SDK)" -ForegroundColor Cyan
Write-Host "--------------------------------" -ForegroundColor Cyan
Write-Host ""
Write-Host "This project includes a Node CLI seeder to test Firestore GET/POST and seed demo data:" -ForegroundColor White
Write-Host "1) Ensure Node.js is installed (https://nodejs.org/)" -ForegroundColor White
Write-Host "2) In project root, run: npm init -y; npm install firebase-admin" -ForegroundColor White
Write-Host "3) Create a Firebase service account and download JSON:" -ForegroundColor White
Write-Host "   Firebase Console > Project Settings > Service accounts > Generate new private key" -ForegroundColor Gray
Write-Host "4) Save it as serviceAccountKey.json in project root OR set env var GOOGLE_APPLICATION_CREDENTIALS to its path" -ForegroundColor White
Write-Host "5) Run the seeder:" -ForegroundColor White
Write-Host "   $env:GOOGLE_APPLICATION_CREDENTIALS = \"C:\\path\\to\\serviceAccountKey.json\"" -ForegroundColor Gray
Write-Host "   node .\\scripts\\seed_firebase_demo.js" -ForegroundColor Gray
Write-Host "" 
Write-Host "Seeder actions:" -ForegroundColor Cyan
Write-Host "   ✅ Tests Firestore connection (write/read)" -ForegroundColor Green
Write-Host "   ✅ Seeds demo user 'harsh' with 6 tasks and verifies GET/POST" -ForegroundColor Green
Write-Host "   ✅ Seeds guest user 'guest_demo' with mock tasks" -ForegroundColor Green
Write-Host "" 
Write-Host "After running, verify in Firebase Console:" -ForegroundColor White
Write-Host "   • users/harsh/tasks/*" -ForegroundColor Gray
Write-Host "   • users/guest_demo/tasks/*" -ForegroundColor Gray
Write-Host "" 
Write-Host "Happy testing! 🎉" -ForegroundColor Green