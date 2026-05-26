# 💰 MonBudget

Application Android de gestion de budget personnel, conçue pour les étudiants.  
Développée en **Kotlin** avec **Jetpack Compose**, architecture **MVVM** et base de données locale **Room**.

---

##  Fonctionnalités

### 🔐 Authentification
- Création de compte avec nom, prénom, email, mot de passe et budget mensuel
- Connexion sécurisée avec validation des identifiants
- Session utilisateur persistante en mémoire

###  Tableau de bord
- Résumé du budget mensuel restant avec barre de progression
- Nombre de dépenses du mois et objectifs en cours
- Aperçu des dernières dépenses
- Navigation fluide entre les écrans

###  Gestion des dépenses
- Ajout d'une dépense avec montant, catégorie, note et lieu
- Localisation GPS automatique ou sélection manuelle sur carte interactive (OpenStreetMap)
- Historique complet avec filtres par catégorie
- Regroupement des dépenses par date (Aujourd'hui / Hier / Date)
- Suppression avec confirmation
- Détail complet de chaque dépense

###  Objectifs d'épargne
- Création d'objectifs avec nom, icône, montant cible, montant épargné et date limite
- Barre de progression pour chaque objectif
- Badge "Atteint" automatique quand l'objectif est rempli
- **Conseils personnalisés par IA (Grok)** basés sur les objectifs et le budget

###  Carte interactive (in-app)
- Sélection d'un lieu directement dans l'application (pas de redirection externe)
- Carte OpenStreetMap via OSMDroid
- Reverse geocoding pour obtenir l'adresse à partir des coordonnées

---

##  Architecture

```
app/
├── data/
│   ├── api/            → Service Grok (IA)
│   ├── database/       → Room Database, DAOs
│   ├── model/          → Entités (User, Expense, Goal, Category)
│   ├── repository/     → Couche d'accès aux données
│   └── session/        → Session utilisateur (singleton)
├── navigation/         → NavGraph, Routes
├── ui/
│   ├── auth/           → Login, Register
│   ├── components/     → Composants réutilisables
│   ├── dashboard/      → Écran principal
│   ├── expense/        → Ajout, historique, détail
│   ├── goals/          → Objectifs, ajout d'objectif
│   └── theme/          → Couleurs, typographie
└── viewmodel/          → ViewModels (Auth, Expense, Goal)
```

**Pattern** : MVVM (Model - View - ViewModel)  
**UI** : Jetpack Compose  
**Base de données** : Room (SQLite)  
**Réactivité** : StateFlow + Coroutines  
**Navigation** : Navigation Compose  

---

## 🛠️ Stack technique

| Technologie | Version | Usage |
|---|---|---|
| Kotlin | 2.0.21 | Langage principal |
| Jetpack Compose | BOM 2025.x | Interface utilisateur |
| Room | 2.7.1 | Base de données locale |
| KSP | 2.0.21-1.0.27 | Génération de code Room |
| Navigation Compose | - | Gestion des écrans |
| OSMDroid | 6.1.20 | Carte interactive in-app |
| Grok API (X.AI) | grok-3-mini | Conseils IA personnalisés |
| Coroutines | - | Appels asynchrones |

---

##  Installation

### Prérequis
- Android Studio Hedgehog ou plus récent
- SDK Android 28+ (Android 9.0 minimum)
- Connexion Internet pour la carte et l'IA

### Étapes

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/adam-hassen/Mobile.git
   cd Mobile
   ```

2. **Configurer la clé API Grok**  
   Dans le fichier `local.properties` à la racine du projet, ajouter :
   ```properties
   GROK_API_KEY=ta_cle_api_ici
   ```
   > Obtenir une clé sur [console.x.ai](https://console.x.ai)

3. **Lancer le projet**  
   Ouvrir dans Android Studio et cliquer sur ▶ Run.

---

## 📂 Configuration

Le fichier `local.properties` n'est pas versionné (ignoré par `.gitignore`).  
Il contient les informations sensibles propres à chaque développeur :
```properties
sdk.dir=...chemin vers le SDK Android...
GROK_API_KEY=...ta clé API Grok...
```

---

##  Aperçu des écrans

| Connexion | Tableau de bord | Dépenses | Objectifs |
|:---------:|:---------------:|:--------:|:---------:|
|  Login  |  Dashboard   |  History |  Goals |

---

##  Auteur

**Adam Hassen**  
Projet réalisé dans le cadre d'un cours de développement mobile.

---

## 📄 Licence

Ce projet est à usage personnel et éducatif.
