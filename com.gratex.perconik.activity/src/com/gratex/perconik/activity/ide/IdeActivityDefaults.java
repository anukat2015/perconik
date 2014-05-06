package com.gratex.perconik.activity.ide;

import static com.gratex.perconik.activity.ide.Internals.*;
import java.net.URL;
import sk.stuba.fiit.perconik.utilities.net.UniformResources;
import com.gratex.perconik.activity.MilestoneResolver;
import com.gratex.perconik.activity.TimeSupplier;
import com.gratex.perconik.services.uaca.vs.IdeEventDto;

public final class IdeActivityDefaults
{
	public static final URL watcherUrl;
	
	static
	{
		watcherUrl  = UniformResources.newUrl("http://localhost:16375");
	}
	
	private static final MilestoneResolver<IdeEventDto> milestoneResolver;
	
	private static final TimeSupplier timeSupplier;
	
	static
	{
		milestoneResolver = options.containsKey("always-milestone") ? Debug.milestoneResolver : Internals.milestoneResolver;
		timeSupplier      = options.containsKey("fixed-year")       ? Debug.timeSupplier      : Internals.timeSupplier;
	}

	private IdeActivityDefaults()
	{
		throw new AssertionError();
	}
	
	public static final MilestoneResolver<IdeEventDto> getMilestoneResolver()
	{
		return milestoneResolver;
	}
	
	public static final TimeSupplier getTimeSupplier()
	{
		return timeSupplier;
	}
}
