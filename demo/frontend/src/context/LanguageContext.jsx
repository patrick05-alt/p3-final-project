import React, { createContext, useState, useContext, useEffect } from 'react';

const translations = {
  en: {
    home: 'Home',
    events: 'Events',
    locations: 'Locations',
    contacts: 'Contacts',
    checklist: 'Checklist',
    announcements: 'Announcements',
    search: 'Search',
    adminPanel: 'Admin Panel',
    profile: 'Profile',
    logout: 'Logout',
    login: 'Login',
    register: 'Register',
    welcome: 'Welcome',
    settings: 'Settings',
    myRSVPs: 'My RSVPs',
    myFavorites: 'My Favorites',
    activityLogs: 'Activity Logs',
    sharedChecklists: 'Shared Checklists',
    darkMode: 'Dark Mode',
    language: 'Language',
    save: 'Save',
    cancel: 'Cancel',
    edit: 'Edit',
    delete: 'Delete',
    create: 'Create',
    update: 'Update',
    loading: 'Loading...',
    error: 'Error',
    success: 'Success'
  },
  es: {
    home: 'Inicio',
    events: 'Eventos',
    locations: 'Ubicaciones',
    contacts: 'Contactos',
    checklist: 'Lista de Verificación',
    announcements: 'Anuncios',
    search: 'Buscar',
    adminPanel: 'Panel de Administración',
    profile: 'Perfil',
    logout: 'Cerrar Sesión',
    login: 'Iniciar Sesión',
    register: 'Registrarse',
    welcome: 'Bienvenido',
    settings: 'Configuración',
    myRSVPs: 'Mis Confirmaciones',
    myFavorites: 'Mis Favoritos',
    activityLogs: 'Registros de Actividad',
    sharedChecklists: 'Listas Compartidas',
    darkMode: 'Modo Oscuro',
    language: 'Idioma',
    save: 'Guardar',
    cancel: 'Cancelar',
    edit: 'Editar',
    delete: 'Eliminar',
    create: 'Crear',
    update: 'Actualizar',
    loading: 'Cargando...',
    error: 'Error',
    success: 'Éxito'
  },
  fr: {
    home: 'Accueil',
    events: 'Événements',
    locations: 'Emplacements',
    contacts: 'Contacts',
    checklist: 'Liste de Contrôle',
    announcements: 'Annonces',
    search: 'Rechercher',
    adminPanel: 'Panneau d\'Administration',
    profile: 'Profil',
    logout: 'Se Déconnecter',
    login: 'Se Connecter',
    register: 'S\'inscrire',
    welcome: 'Bienvenue',
    settings: 'Paramètres',
    myRSVPs: 'Mes Confirmations',
    myFavorites: 'Mes Favoris',
    activityLogs: 'Journaux d\'Activité',
    sharedChecklists: 'Listes Partagées',
    darkMode: 'Mode Sombre',
    language: 'Langue',
    save: 'Enregistrer',
    cancel: 'Annuler',
    edit: 'Modifier',
    delete: 'Supprimer',
    create: 'Créer',
    update: 'Mettre à Jour',
    loading: 'Chargement...',
    error: 'Erreur',
    success: 'Succès'
  }
};

const LanguageContext = createContext();

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within LanguageProvider');
  }
  return context;
};

export const LanguageProvider = ({ children }) => {
  const [language, setLanguage] = useState(() => {
    return localStorage.getItem('language') || 'en';
  });

  useEffect(() => {
    localStorage.setItem('language', language);
  }, [language]);

  const t = (key) => {
    return translations[language][key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
};
