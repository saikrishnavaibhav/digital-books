package com.digitalbooks.requests;

import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@Lob
	private String logo;
	
	@NotBlank
	@Size(min = 3, max = 50)
	private String title;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String category;
	
	@NotNull
	private Long price;
	

	private String authorName;
	
	private int authorId;

	private Date publishedDate;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String publisher;
	
	private boolean active;
	
	@NotBlank
	@Size(min = 50, max = 2000)
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(active, authorId, authorName, category, content, id, logo, price,
				publishedDate, publisher, title);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return active == other.active && authorId == other.authorId && Objects.equals(authorName, other.authorName)
				&& Objects.equals(category, other.category) && Objects.equals(content, other.content) && id == other.id
				&& Objects.equals(logo, other.logo) && Objects.equals(price, other.price)
				&& Objects.equals(publishedDate, other.publishedDate) && Objects.equals(publisher, other.publisher)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", logo=" + logo + ", title=" + title + ", category=" + category
				+ ", price=" + price + ", authorName=" + authorName + ", authorId=" + authorId + ", publishedDate="
				+ publishedDate + ", publisher=" + publisher + ", active=" + active + ", content=" + content + "]";
	}

}
