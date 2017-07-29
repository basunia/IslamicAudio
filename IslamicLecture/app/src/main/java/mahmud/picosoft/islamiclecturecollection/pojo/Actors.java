package mahmud.picosoft.islamiclecturecollection.pojo;

public class Actors {
	private String id;
	private String name;
	private String link;
	private String contributor;
	private String time;

	public Actors() {

	}

	public Actors(String name, String link, String contributor, String time, String _id) {
		super();
		this.id = _id;
		this.name = name;
		this.link = link;
		this.contributor = contributor;
		this.time = time;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
