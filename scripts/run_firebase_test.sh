#!/bin/bash

# Firebase Connection Test Script
# This script helps test Firebase connectivity and populate sample data

echo "🚀 Firebase Connection Test Script"
echo "=================================="
echo ""

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found. Please install it first:"
    echo "   npm install -g firebase-tools"
    echo ""
    exit 1
fi

echo "✅ Firebase CLI found"

# Check if user is logged in
if ! firebase projects:list &> /dev/null; then
    echo "❌ Not logged in to Firebase. Please login first:"
    echo "   firebase login"
    echo ""
    exit 1
fi

echo "✅ Firebase authentication verified"

# List available projects
echo ""
echo "📋 Available Firebase projects:"
firebase projects:list

echo ""
echo "🔧 To use this test script with your TaskApp:"
echo ""
echo "1. Make sure your google-services.json is in the app/ directory"
echo "2. Build and run the app:"
echo "   ./gradlew :app:assembleDebug"
echo "   ./gradlew :app:installDebug"
echo ""
echo "3. In the app, navigate to Settings > Firebase Test"
echo "   Or add the FirebaseTestScreen to your navigation"
echo ""
echo "4. Tap 'Run Firebase Test' to:"
echo "   ✅ Test Firestore connection"
echo "   ✅ Create 3 demo users (Premium, Free, Guest)"
echo "   ✅ Populate colorful sample tasks"
echo "   ✅ Verify data retrieval"
echo ""
echo "5. The sample data will showcase:"
echo "   🎨 8 different task category colors"
echo "   ⭐ Important task indicators"
echo "   📊 Progress tracking (0-100%)"
echo "   ✅ Completed vs pending tasks"
echo "   🏷️ Task tags and priorities"
echo ""
echo "6. Use 'Cleanup Test Data' to remove sample data"
echo ""
echo "🎯 This demonstrates the enhanced UI features:"
echo "   • Colorful task cards with category backgrounds"
echo "   • Staggered grid layout"
echo "   • Search and filter functionality"
echo "   • Selection mode for bulk operations"
echo "   • Beautiful statistics with progress indicators"
echo ""
echo "Happy testing! 🎉"