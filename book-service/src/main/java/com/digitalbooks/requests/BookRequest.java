package com.digitalbooks.requests;

import java.sql.Timestamp;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BookRequest {

	Long id;
	
	private String logo;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String category;
	
	@NotBlank
	@Size(min = 3, max = 50)
	private String title;
	
	@NotNull
	private Long price;
	
	private Long authorId;
	
	private Timestamp publishedDate;
	
	@NotBlank
	@Size(min = 50, max = 2000)
	private String content;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String authorName;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String publisher;
	
	private boolean active = true;

	public String getLogo() {
		return logo;
	}
	
	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Long getAuthorId() {
		return authorId;
	}
	
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Timestamp getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Timestamp publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookRequest other = (BookRequest) obj;
		return active == other.active && Objects.equals(authorId, other.authorId)
				&& Objects.equals(authorName, other.authorName) && Objects.equals(category, other.category)
				&& Objects.equals(content, other.content) && Objects.equals(id, other.id)
				&& Objects.equals(logo, other.logo) && Objects.equals(price, other.price)
				&& Objects.equals(publishedDate, other.publishedDate) && Objects.equals(publisher, other.publisher)
				&& Objects.equals(title, other.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(active, authorId, authorName, category, content, id, logo, price, publishedDate, publisher,
				title);
	}

	@Override
	public String toString() {
		return "BookRequest [id=" + id + ", logo=" + logo + ", title=" + title + ", category=" + category + ", price="
				+ price + ", authorId=" + authorId + ", authorName=" + authorName + ", publisher=" + publisher
				+ ", publishedDate=" + publishedDate + ", content=" + content + ", active=" + active + "]";
	}
	
}
