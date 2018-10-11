package com.github.birdgeek.breadbot.notifiers;

import java.time.Duration;
import java.time.Instant;

public class StreamStats {
	private Integer viewerTotal  = 0;
	private Integer viewerChecks = 0;
	private Integer viewerPeak   = 0;
	private Instant startTime;
	
	StreamStats() {
		startTime = Instant.now();
	}
	
	public Integer getViewerAverage() {
		if(viewerChecks == 0) {
			return 0;
		}
		return ((Integer)(viewerTotal / viewerChecks));
	}
	
	public Integer getViewerPeak() {
		return viewerPeak;
	}
	
	public Duration getStreamDuration() {
		Instant current = Instant.now();
		return Duration.between(startTime, current);
	}
	
	public String getStreamDurationString() {
		String r = "";
		
		Duration duration = this.getStreamDuration();
		long seconds = duration.getSeconds();
		
		r = (seconds % 60) + "s";
		if(seconds > 60) {
			long minutes = duration.toMinutes();
			r = (minutes % 60) + "m" + r;
			if(minutes > 60) {
				long hours = duration.toHours();
				r = (hours % 24) + "h" + r;
				if(hours > 24) {
					long days = duration.toDays();
					r = days + "d" + r;
				}
			}
		}
		
		return r;
	}
	
	public void addViewers(Integer i) {
		viewerTotal += i;
		viewerChecks += 1;
		if(i > viewerPeak) {
			viewerPeak = i;
		}
	}
}
