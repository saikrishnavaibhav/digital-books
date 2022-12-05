package com.digitalbooks.responses;

import java.util.Date;
import java.util.Objects;

public class BookResponse {

	private Long id;

	private String logo;

	private String title;

	private String category;

	private Long price;

	private Long authorId;

	private String authorName;

	private String publisher;

	private Date publishedDate;

	private String content;

	private boolean active;

	public BookResponse() {
		
	}
	
	public BookResponse(Long id, String logo, String title, String category, Long price, Long authorId, String authorName,
			String publisher, Date publishedDate, String content, boolean active) {
		super();
		this.id = id;
		this.logo = logo;
		this.title = title;
		this.category = category;
		this.price = price;
		this.authorId = authorId;
		this.authorName = authorName;
		this.publisher = publisher;
		this.publishedDate = publishedDate;
		this.content = content;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String value) {
		this.category = value;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String value) {
		this.publisher = value;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String value) {
		this.content = value;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean value) {
		this.active = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(active, authorId, authorName, category, content, id, logo, price, publishedDate, publisher,
				title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookResponse other = (BookResponse) obj;
		return active == other.active && authorId == other.authorId && Objects.equals(authorName, other.authorName)
				&& Objects.equals(category, other.category) && Objects.equals(content, other.content) && id == other.id
				&& Objects.equals(logo, other.logo) && Objects.equals(price, other.price)
				&& Objects.equals(publishedDate, other.publishedDate) && Objects.equals(publisher, other.publisher)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "BookResponse [id=" + id + ", logo=" + logo + ", title=" + title + ", category=" + category + ", price="
				+ price + ", authorId=" + authorId + ", authorName=" + authorName + ", publisher=" + publisher
				+ ", publishedDate=" + publishedDate + ", content=" + content + ", active=" + active + "]";
	}

}
