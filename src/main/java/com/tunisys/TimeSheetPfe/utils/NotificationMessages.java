package com.tunisys.TimeSheetPfe.utils;

/**
 * Utility class for centralized notification messages in French
 * Centralizes all notification text to ensure consistency and easy maintenance
 */
public class NotificationMessages {

    // Task-related notifications
    public static class Tasks {
        public static final String NEW_TASK_ASSIGNED_TITLE = "📋 Nouvelle tâche assignée";
        
        public static String newTaskAssignedBody(String taskTitle, String taskDescription) {
            return String.format("Vous avez été assigné(e) à la tâche \"%s\" - %s", taskTitle, taskDescription);
        }
        
        public static final String TASK_STATUS_UPDATED_TITLE = "🔄 Statut de tâche mis à jour";
        
        public static String taskStatusUpdatedBody(String taskTitle, String status) {
            return String.format("Le statut de la tâche \"%s\" a été mis à jour à %s", taskTitle, status);
        }
        
        public static final String TASK_APPROVED_TITLE = "✅ Tâche APPROUVÉE";
        public static final String TASK_DISAPPROVED_TITLE = "⚠️ Tâche DÉSAPPROUVÉE";
        
        public static String taskApprovedBody(String taskTitle) {
            return String.format("Votre tâche \"%s\" a été approuvée par le manager.", taskTitle);
        }
        
        public static String taskDisapprovedBody(String taskTitle) {
            return String.format("Votre tâche \"%s\" a été désapprouvée par le manager.", taskTitle);
        }
        
        public static final String TASK_DELETED_TITLE = "🗑️ Tâche supprimée";
        
        public static String taskDeletedBody(String taskTitle) {
            return String.format("La tâche \"%s\" à laquelle vous étiez assigné(e) a été supprimée.", taskTitle);
        }
    }

    // Project-related notifications
    public static class Projects {
        public static final String NEW_PROJECT_ASSIGNED_TITLE = "🚀 Nouveau projet assigné";
        
        public static String newProjectAssignedBody(String projectName, String projectDescription) {
            return String.format("Vous avez été assigné(e) au projet \"%s\" - %s", projectName, projectDescription);
        }
        
        public static final String ADDED_TO_PROJECT_TITLE = "➕ Ajouté(e) à un projet";
        
        public static String addedToProjectBody(String projectName, String projectDescription) {
            return String.format("Vous avez été ajouté(e) au projet \"%s\" - %s", projectName, projectDescription);
        }
        
        public static final String PROJECT_ASSIGNMENT_REQUEST_TITLE = "📝 Demande d'assignation à un projet";
        
        public static String projectAssignmentRequestBodyForAdmin(String employeeName, String employeeEmail) {
            return String.format("L'employé(e) %s (%s) demande à être assigné(e) à un projet.", employeeName, employeeEmail);
        }
        
        public static String projectAssignmentRequestBodyForManager(String employeeName, String employeeEmail) {
            return String.format("L'employé(e) %s (%s) demande à être assigné(e) à un projet. " +
                    "Vous pouvez l'ajouter à l'un de vos projets si vous avez besoin de personnel supplémentaire.", 
                    employeeName, employeeEmail);
        }
        
        public static String projectAssignmentRequestSuccessMessage(int notificationCount) {
            return String.format("Votre demande a été envoyée à %d administrateur(s) et manager(s).", notificationCount);
        }
    }

    // Leave-related notifications
    public static class Leaves {
        public static final String LEAVE_REQUEST_CREATED_TITLE = "📅 Demande de congé créée";
        public static final String LEAVE_REQUEST_CREATED_BODY = "Votre demande de congé a été soumise et est en attente d'approbation.";
        
        public static final String LEAVE_REQUEST_APPROVED_TITLE = "✅ Demande de congé approuvée";
        public static final String LEAVE_REQUEST_APPROVED_BODY = "Votre demande de congé a été approuvée par votre manager.";
        
        public static final String LEAVE_REQUEST_REJECTED_TITLE = "❌ Demande de congé refusée";
        
        public static String leaveRequestRejectedBody(String rejectionReason) {
            return String.format("Votre demande de congé a été refusée. Motif : %s", rejectionReason);
        }
        
        public static final String NEW_LEAVE_REQUEST_FOR_MANAGER_TITLE = "📋 Nouvelle demande de congé";
        
        public static String newLeaveRequestForManagerBody(String employeeName, String startDate, String endDate, String leaveType) {
            return String.format("%s a soumis une demande de congé (%s) du %s au %s.", 
                    employeeName, leaveType, startDate, endDate);
        }
    }

    // General notifications
    public static class General {
        public static final String WELCOME_TITLE = "👋 Bienvenue !";
        public static final String WELCOME_BODY = "Bienvenue dans l'application de gestion des feuilles de temps !";
        
        public static final String SYSTEM_MAINTENANCE_TITLE = "🔧 Maintenance système";
        public static final String SYSTEM_MAINTENANCE_BODY = "Une maintenance système est prévue. Veuillez sauvegarder votre travail.";
        
        public static final String DEADLINE_REMINDER_TITLE = "⏰ Rappel d'échéance";
        
        public static String deadlineReminderBody(String projectName, int daysRemaining) {
            return String.format("Le projet \"%s\" arrive à échéance dans %d jour(s). " +
                    "Assurez-vous que toutes les tâches sont en cours.", projectName, daysRemaining);
        }
        
        public static String deadlineReminderBodyForManager(String projectName, int daysRemaining) {
            return String.format("Le projet \"%s\" arrive à échéance dans %d jour(s). " +
                    "Assurez-vous que tous les membres de l'équipe terminent leurs tâches.", projectName, daysRemaining);
        }
    }

    // Error messages
    public static class Errors {
        public static final String NOTIFICATION_SEND_ERROR = "Erreur lors de l'envoi de la notification";
        public static final String FCM_TOKEN_ERROR = "Erreur avec le token FCM";
        public static final String USER_NOT_FOUND_ERROR = "Utilisateur non trouvé";
    }

    // Success messages
    public static class Success {
        public static final String NOTIFICATION_SENT = "Notification envoyée avec succès";
        public static final String CONNECTION_ADDED = "Connexion ajoutée avec succès";
        public static final String CONNECTION_REMOVED = "Connexion supprimée avec succès";
        public static final String NOTIFICATIONS_MARKED_SEEN = "Notifications marquées comme vues";
    }

    // Action URLs
    public static class ActionUrls {
        public static String taskUrl(Long taskId) {
            return "task/" + taskId;
        }
        
        public static String projectUrl(Long projectId) {
            return "project/" + projectId;
        }
        
        public static String adminEmployeeUrl(Long userId) {
            return "/admin/employees/" + userId;
        }
        
        public static String managerEmployeeUrl(Long userId) {
            return "/manager/employees/" + userId;
        }
        
        public static final String DASHBOARD_URL = "dashboard";
        public static final String LEAVES_URL = "leaves";
    }
}
