package com.casaflow.user.domain;

public enum Permission {
    // Leads
    LEAD_VIEW,
    LEAD_CREATE,
    LEAD_EDIT,
    LEAD_DELETE,
    LEAD_ASSIGN,
    LEAD_EXPORT,

    // Properties
    PROPERTY_VIEW,
    PROPERTY_CREATE,
    PROPERTY_EDIT,
    PROPERTY_DELETE,
    PROPERTY_PUBLISH,

    // Documents
    DOCUMENT_VIEW,
    DOCUMENT_UPLOAD,
    DOCUMENT_DELETE,

    // Agenda
    AGENDA_VIEW,
    AGENDA_CREATE,
    AGENDA_EDIT,
    AGENDA_DELETE,

    // Reports
    REPORT_VIEW,
    REPORT_EXPORT,

    // Users & Teams
    USER_VIEW,
    USER_CREATE,
    USER_EDIT,
    USER_DELETE,
    USER_MANAGE_ROLES,
    TEAM_MANAGE,

    // Settings
    SETTINGS_COMPANY,
    SETTINGS_SUBSCRIPTION,
    SETTINGS_INTEGRATIONS
}
