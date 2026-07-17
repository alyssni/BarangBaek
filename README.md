# Group Members of ADUDU CDCS2304A
1. [2024666468] AMIRA ADANI BINTI SABARUDIN 
2. [2024208934] EYVA GREATY GEOFFREY 
3. [2024290064] ISABELA NURSOFEA BINTI NOOR AZMAN 
4. [2024661914] NICOLE MARSHA BINTI LUCIUS 
5. [2024800516] NUR ALYSSA HANI BINTI RIDUAN 

# BarangBaek Setup Guide

BarangBaek is developed using:

* NetBeans 8.2
* JDK 8
* GlassFish 4.1.1
* Java DB / Apache Derby

## 1. Download the Project

Download the project from GitHub:

```text
Code → Download ZIP
```

Extract the ZIP and open the `BarangBaek` folder in NetBeans:

```text
File → Open Project
```

## 2. Database Files

The project contains:

```text
database/barangbaek.sql
database/backup/barangbaek_db-backup.zip
```

`barangbaek.sql` only recreates the database tables and structure.

`barangbaek_db-backup.zip` contains the complete database, including the existing data.

To get the same database used in the system, restore the ZIP backup.

## 3. Restore the Complete Database

### Step 1: Stop Java DB

In NetBeans:

```text
Services → Databases → Right-click Java DB → Stop Server
```

### Step 2: Find the Database Location

Go to:

```text
Services → Databases → Right-click Java DB → Properties
```

Check the `Database Location`.

It is usually:

```text
C:\Users\<username>\.netbeans-derby
```

### Step 3: Extract the Backup

Extract:

```text
database/backup/barangbaek_db-backup.zip
```

The extracted folder should be:

```text
barangbaek_db
```

Inside it, there should be files such as:

```text
service.properties
seg0
log
```

### Step 4: Copy the Database

Copy the entire extracted `barangbaek_db` folder into the Java DB Database Location.

Correct structure:

```text
.netbeans-derby
└── barangbaek_db
    ├── service.properties
    ├── seg0
    └── log
```

Do not create a double folder such as:

```text
barangbaek_db\barangbaek_db
```

### Step 5: Start Java DB

```text
Services → Databases → Right-click Java DB → Start Server
```

### Step 6: Connect to the Database

Use:

```text
Database: barangbaek_db
Username: app
Password: app
URL: jdbc:derby://localhost:1527/barangbaek_db
```

If the connection is not available:

```text
Services → Databases → Right-click Databases → New Connection
```

Choose `Java DB (Network)` and enter:

```text
Host: localhost
Port: 1527
Database: barangbaek_db
Username: app
Password: app
```

After connecting, expand:

```text
APP → Tables
```

The tables and existing data should appear automatically.

Do not execute `barangbaek.sql` after restoring the ZIP backup because the database already contains the tables.

## 4. Alternative: Create an Empty Database

Use this only when the complete backup is not restored.

Create a database named:

```text
barangbaek_db
```

Use:

```text
Username: app
Password: app
```

Then open and execute:

```text
database/barangbaek.sql
```

This creates the table structure only. It does not restore existing users, items, orders or other records.

## 5. Set Up GlassFish

If NetBeans shows `Missing Server`:

```text
Right-click BarangBaek
→ Resolve Missing Server Problem
→ Select GlassFish Server 4.1.1
```

Alternatively:

```text
Right-click BarangBaek
→ Properties
→ Run
→ Server
→ GlassFish Server 4.1.1
```

## 6. Build and Run

Make sure Java DB and GlassFish are running.

Then:

```text
Right-click BarangBaek → Clean and Build
```

After the build succeeds:

```text
Right-click BarangBaek → Run
```

## Database Backup Information

The complete backup was created from the working Java DB using:

```sql
CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(
    'C:/backup-location'
);
```

The generated `barangbaek_db` backup folder was then compressed into:

```text
database/backup/barangbaek_db-backup.zip
```

Therefore:

```text
barangbaek.sql = database structure only
barangbaek_db-backup.zip = database structure and existing data
```
