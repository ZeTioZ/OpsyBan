package fr.opsycraft.opsyban;

public class Ban {
	
	/*
	 * The ban time has to be input in milliseconds!
	 * So make sure to convert any value in milliseconds before calling theses methods!
	 */
	public Ban(String playerName, String giverName, String playerUUID, String reason, long banDate, long banTime)
	{
		this.setPlayerName(playerName);
		this.setGiverName(giverName);
		this.setPlayerUUID(playerUUID);
		this.setReason(reason);
		this.setTime(banDate);
		this.setBanTime(banTime);
	}
	
	public Ban(String playerName, String giverName, String playerUUID, String reason, long banTime)
	{
		this.setPlayerName(playerName);
		this.setGiverName(giverName);
		this.setPlayerUUID(playerUUID);
		this.setReason(reason);
		this.setTime(System.currentTimeMillis());
		this.setBanTime(banTime);
	}
	
	public Ban(String playerName, String giverName, String playerUUID, String reason)
	{
		this.setPlayerName(playerName);
		this.setGiverName(giverName);
		this.setPlayerUUID(playerUUID);
		this.setReason(reason);
		this.setTime(System.currentTimeMillis());
		this.setBanTime(0);
	}
	
	
	private String playerName;
	//region Player Name (Getter/Setter)
	public String getPlayerName()
	{
		return this.playerName;
	}
	
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	//endregion

	private String playerUUID;
	//region Player UUID (Getter/Setter)
	public String getPlayerUUID()
	{
		return this.playerUUID;
	}

	public void setPlayerUUID(String playerUUID)
	{
		this.playerUUID = playerUUID;
	}
	//endregion
	
	private String giverName;
	//region Player Name (Getter/Setter)
	public String getGiverName()
	{
		return this.giverName;
	}
	
	public void setGiverName(String giverName)
	{
		this.giverName = giverName;
	}
	//endregion
	
	private String reason;
	//region Reason (Getter/Setter)
	public String getReason()
	{
		return this.reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}
	//endregion

	private long time; // Time in second of the ban
	//region Time (Getter/Setter)
	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	//endregion

	private long banTime; // Time when the player got banned
	//region Ban Time (Getter/Setter)
	public long getBanTime() {
		return this.banTime;
	}
	
	public void setBanTime(long banTime) {
		this.banTime = banTime;
	}
	//endregion
}
