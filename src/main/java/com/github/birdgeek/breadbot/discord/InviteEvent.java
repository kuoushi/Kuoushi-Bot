package com.github.birdgeek.breadbot.discord;


//import net.dv8tion.jda.core.events.InviteReceivedEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
//import net.dv8tion.jda.core.utils.InviteUtil;

public class InviteEvent extends ListenerAdapter {

/*	public void onInviteReceived(InviteReceivedEvent event) { 
		if (DiscordUtility.isApprovedUser(event.getAuthor().getUsername())) {
//			DiscordMain.jda event.getInvite()
//			InviteUtil.join(event.getInvite(), DiscordMain.jda, null);
//			DiscordMain.discordLog.info("Joined a new server named: " + event.getInvite().getGuildName());
		}
	}
*/	
	public void onGuildJoin(GuildJoinEvent event) {
//		event.getJDA().getTextChannelById(event.getGuild().getPublicChannel().getId()).sendMessage("Hello World! I am **Bread Bot**");
	}

}
