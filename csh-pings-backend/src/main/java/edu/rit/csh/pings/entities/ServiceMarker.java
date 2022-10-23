package edu.rit.csh.pings.entities;

/**
 * Marker interface for reflection purposes
 */
public sealed interface ServiceMarker permits 
	BasicSMSServiceConfiguration,
	DiscordServiceConfiguration,
	EmailServiceConfiguration,
	TelegramServiceConfiguration,
	WebNotificationConfiguration {

}
