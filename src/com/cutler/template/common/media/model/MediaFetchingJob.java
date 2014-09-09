package com.cutler.template.common.media.model;

import java.util.ArrayList;
import java.util.List;

import com.cutler.template.common.media.goal.AbstractMediaFetchingGoal;

public class MediaFetchingJob {
	// 与当前任务关联的所有回调。
	private List<AbstractMediaFetchingGoal> goals = new ArrayList<AbstractMediaFetchingGoal>();
	private boolean bypass;
	private String desc;

	public MediaFetchingJob(String desc, AbstractMediaFetchingGoal goal, boolean bypass) {
		this.bypass = bypass;
		this.desc = desc;
		if (goal != null) {
			this.goals.add(goal);
		}
	}

	public List<AbstractMediaFetchingGoal> getGoals() {
		return goals;
	}

	public void setGoals(List<AbstractMediaFetchingGoal> goals) {
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
