package querying;

import querying.crossref.CrossRefLoader;
import querying.ieeexplore.IEEEXPloreLoader;
import querying.scienceDirect.ScienceDirectLoader;
import querying.scopus.ScopusLoader;
import querying.springer.SpringerLoader;

public enum DataSource {
	CrossRef(CrossRefLoader.class),
	IEEEXplore(IEEEXPloreLoader.class),
	ScienceDirect(ScienceDirectLoader.class),
	Scopus(ScopusLoader.class),
	Springer(SpringerLoader.class)
	;
	
	public final Class<?> loaderClass;

	private DataSource(Class<?> loaderClass) {
		this.loaderClass = loaderClass;
	}
}
