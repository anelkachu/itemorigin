package itemorigin.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import itemorigin.service.CacheService;

@Component
public class ScheduledTasks {

	@Autowired
	CacheService cacheService;

	@Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
	public void scheduleFixedDelayTask() {
		cacheService.getAbroadCounter().set(0);
	}
}
