/**
 * Firebase CLI Seeder (Node + Admin SDK)
 *
 * Purpose:
 * - Test Firestore connection (write/read)
 * - Seed demo user 'harsh' with six tasks and verify GET/POST
 * - Seed guest user 'guest_demo' with mock-style tasks
 *
 * Requirements:
 * - Install Node.js
 * - npm install firebase-admin
 * - Service account JSON: set env GOOGLE_APPLICATION_CREDENTIALS to its path
 *   or place file at ./serviceAccountKey.json
 */

const path = require('path');
const fs = require('fs');
const admin = require('firebase-admin');

function resolveServiceAccountPath() {
  const fromEnv = process.env.GOOGLE_APPLICATION_CREDENTIALS;
  if (fromEnv && fs.existsSync(fromEnv)) return fromEnv;
  const local = path.resolve(process.cwd(), 'serviceAccountKey.json');
  if (fs.existsSync(local)) return local;
  throw new Error('Service account JSON not found. Set GOOGLE_APPLICATION_CREDENTIALS or place serviceAccountKey.json in project root.');
}

function initFirebase() {
  const saPath = resolveServiceAccountPath();
  const serviceAccount = require(saPath);
  const projectId = process.env.FIREBASE_PROJECT_ID || serviceAccount.project_id;
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    projectId,
  });
  return admin.firestore();
}

async function testConnection(db) {
  const docRef = db.collection('test').doc('cli_connection');
  await docRef.set({ timestamp: Date.now(), status: 'connected-cli' });
  const snap = await docRef.get();
  if (!snap.exists) throw new Error('Connection test read-back failed');
  await docRef.delete();
  console.log('✅ Firestore connection ok (CLI)');
}

async function ensureUserDoc(db, userId, displayName, isGuest = false) {
  const userRef = db.collection('users').doc(userId);
  await userRef.set({
    id: userId,
    displayName,
    isGuest,
    subscriptionType: isGuest ? 'FREE' : 'FREE',
    createdAtEpochMillis: Date.now(),
    updatedAtEpochMillis: Date.now(),
  }, { merge: true });
}

function createTask({
  title,
  description,
  categoryId,
  isImportant,
  progress,
  isDone,
  priority,
  dueAtEpochMillis,
  tags,
  completedAtEpochMillis,
}) {
  const base = {
    title,
    description,
    categoryId,
    isImportant,
    progress,
    isDone,
    priority,
    tags,
    createdAtEpochMillis: Date.now(),
    updatedAtEpochMillis: Date.now(),
    version: 1,
  };
  if (dueAtEpochMillis != null) base.dueAtEpochMillis = dueAtEpochMillis;
  if (completedAtEpochMillis != null) base.completedAtEpochMillis = completedAtEpochMillis;
  return base;
}

async function seedHarsh(db) {
  const userId = 'harsh';
  await ensureUserDoc(db, userId, 'Harsh Demo', false);
  const tasks = [
    createTask({
      title: '🌅 Morning Routine',
      description: 'Meditate 10m; Hydrate; Read 5 pages; Brew coffee',
      categoryId: 0, isImportant: true, progress: 50, isDone: false, priority: 2,
      dueAtEpochMillis: null, tags: ['morning', 'routine', 'wellness']
    }),
    createTask({
      title: '💻 Work Tasks',
      description: 'Emails; brainstorm features; review code; push GitHub',
      categoryId: 4, isImportant: false, progress: 0, isDone: false, priority: 1,
      dueAtEpochMillis: null, tags: ['work', 'coding', 'inbox']
    }),
    createTask({
      title: '🍴 Lunch Break',
      description: 'Prepare healthy meal; Podcast; Short walk',
      categoryId: 3, isImportant: false, progress: 0, isDone: false, priority: 0,
      dueAtEpochMillis: null, tags: ['health', 'meal', 'break']
    }),
    createTask({
      title: '📚 Study Session',
      description: 'Revise DS; Solve 3 problems; Watch Jetpack tutorial',
      categoryId: 2, isImportant: true, progress: 0, isDone: false, priority: 2,
      dueAtEpochMillis: null, tags: ['study', 'android', 'ds']
    }),
    createTask({
      title: '🏋️‍♀️ Evening Workout',
      description: 'Run 3 km; Upper body; Protein shake',
      categoryId: 5, isImportant: false, progress: 25, isDone: false, priority: 1,
      dueAtEpochMillis: null, tags: ['fitness', 'run']
    }),
    createTask({
      title: '🎬 Relax & Unwind',
      description: 'Netflix; Friends; Sleep before 11 PM',
      categoryId: 7, isImportant: false, progress: 0, isDone: false, priority: 0,
      dueAtEpochMillis: null, tags: ['relax', 'friends', 'sleep']
    }),
  ];

  let ok = 0;
  for (let i = 0; i < tasks.length; i++) {
    const taskRef = db.collection('users').doc(userId).collection('tasks').doc(`harsh_task_${i + 1}`);
    await taskRef.set(tasks[i]); // POST
    const snap = await taskRef.get(); // GET
    if (snap.exists) ok++; else throw new Error(`GET failed for harsh_task_${i + 1}`);
  }
  console.log(`✅ Seeded ${ok}/${tasks.length} tasks for 'harsh' (GET/POST verified)`);
}

async function seedGuest(db) {
  const userId = 'guest_demo';
  await ensureUserDoc(db, userId, 'Guest User', true);
  const tasks = [
    createTask({
      title: '🌅 Morning Routine', description: 'Meditate 10m; Hydrate; Read; Coffee',
      categoryId: 0, isImportant: true, progress: 50, isDone: false, priority: 2,
      dueAtEpochMillis: null, tags: ['demo', 'morning']
    }),
    createTask({
      title: '💻 Work Tasks', description: 'Emails; brainstorm; review; push',
      categoryId: 4, isImportant: false, progress: 0, isDone: false, priority: 1,
      dueAtEpochMillis: null, tags: ['demo', 'work']
    }),
    createTask({
      title: '🏋️‍♀️ Evening Workout', description: 'Run; Strength; Protein',
      categoryId: 5, isImportant: false, progress: 25, isDone: false, priority: 1,
      dueAtEpochMillis: null, tags: ['demo', 'fitness']
    }),
  ];

  for (let i = 0; i < tasks.length; i++) {
    const taskRef = db.collection('users').doc(userId).collection('tasks').doc(`guest_mock_${i + 1}`);
    await taskRef.set(tasks[i]);
  }
  console.log(`✅ Seeded ${tasks.length} mock tasks for 'guest_demo'`);
}

async function main() {
  try {
    const db = initFirebase();
    await testConnection(db);
    await seedHarsh(db);
    await seedGuest(db);
    console.log('🎉 Done. Check Firestore: users/harsh/tasks/* and users/guest_demo/tasks/*');
    process.exit(0);
  } catch (err) {
    console.error('❌ CLI seeder failed:', err.message);
    process.exit(1);
  }
}

main();