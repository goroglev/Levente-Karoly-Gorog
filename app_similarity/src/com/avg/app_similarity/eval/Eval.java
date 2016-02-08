package com.avg.app_similarity.eval;

public enum Eval {

	NONE(""),
	AUC("com.avg.app_similarity.eval.AUC"),
	RankedScoring("com.avg.app_similarity.eval.RankedScoring");
	
	private Eval(String className)  {
		try {
			this.clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			this.clazz = null;
		}
	}
	
	public Class<?> clazz;
	
}
