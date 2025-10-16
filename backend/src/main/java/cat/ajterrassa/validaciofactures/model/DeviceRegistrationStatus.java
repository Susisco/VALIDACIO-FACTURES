package cat.ajterrassa.validaciofactures.model;

public enum DeviceRegistrationStatus {
    PENDING,
    APPROVED,
    REVOKED,
    ARCHIVED, // inactivitat prolongada, reactivable
    DELETED   // baixa lògica per conservació d'històric
}
