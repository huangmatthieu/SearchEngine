package modeles;

import indexation.Index;

public abstract class MetaModel<T> extends IRmodel{
	public MetaModel(Index idx) {
		super(idx);
	}

	protected FeaturersList list; 
}
