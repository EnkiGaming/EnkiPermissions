package latmod.enkigaming.perms.json;

import com.google.gson.annotations.Expose;

public class Rank
{
	@Expose public Boolean isDefault;
	@Expose public String prefix;
	@Expose public String suffix;
	@Expose public String include[];
	@Expose public String permissions[];
	
	public void setDefaults()
	{
		isDefault = null;
		prefix = "";
		suffix = "";
		include = new String[0];
		permissions = new String[0];
	}
}