package org.dhbw.mosbach.ai.tickets.model;

/**
 * Klasse von Benutzer-Rollen. Sie wird final angelegt, da diese sich nicht ändern.
 * Jeder Rolle stehen bestimmte Berechtigungen zu (Was diese auf der Webseite sehen und machen dürfen).
 *
 * Es gibt drei verschiedene Basis-Rollen:
 * - Administrator: Mitarbeiter von Ticket-Master, verwaltet Benutzeraccounts (anlegen, löschen, etc.).
 * - Bearbeiter: Mitarbeiter von Ticket-Master, bearbeitet eingehende Tickets (bearbeiten, delegieren, schließen, etc.).
 * - Kunde: Externer Nutzer des Ticket-Systems, reicht Tickets ein und beobachtet deren Verlauf.
 */
public final class Roles {
	public static final String ADMIN = "admin";
	public static final String EDITOR = "editor";
	public static final String CUSTOMER = "customer";
}
