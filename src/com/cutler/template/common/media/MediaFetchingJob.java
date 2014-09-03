package com.cutler.template.common.media;

import java.util.ArrayList;
import java.util.List;

public class MediaFetchingJob {
	// 与当前任务关联的所有回调。
	private List<MediaFetchingGoal> goals = new ArrayList<MediaFetchingGoal>();
	private boolean bypass;
	private String desc;

	public MediaFetchingJob(String desc, MediaFetchingGoal goal, boolean bypass) {
		this.bypass = bypass;
		this.desc = desc;
		if (goal != null) {
			this.goals.add(goal);
		}
	}

	public List<MediaFetchingGoal> getGoals() {
		return goals;
	}

	public void setGoals(List<MediaFetchingGoal> goals) {
		this.goals = goals;
	}

	public boolean isBypass() {
		return bypass;
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}

	public String getDesc() {
		return desc;
	}
}
