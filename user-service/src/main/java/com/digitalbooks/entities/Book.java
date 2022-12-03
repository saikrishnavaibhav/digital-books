package com.digitalbooks.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@NotBlank
	private String logo;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String title;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String category;
	
	@NotNull
	private Long price;
	
	//@NotBlank
	private int authorId;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String publisher;
	
	//@NotBlank
	private Date publishedDate;
	
	@NotBlank
	@Size(min = 50, max = 500)
	private String content;
	
	private boolean active;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String value) {
		this.logo = value;
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

	public long getPrice() {
		return price;
	}

	public void setPrice(long value) {
		this.price = value;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
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
		return Objects.hash(active, authorId, category, content, id, logo, price, publishedDate, publisher, title);
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
		return active == other.active && Objects.equals(authorId, other.authorId)
				&& Objects.equals(category, other.category) && Objects.equals(content, other.content) && id == other.id
				&& Objects.equals(logo, other.logo) && price == other.price
				&& Objects.equals(publishedDate, other.publishedDate) && Objects.equals(publisher, other.publisher)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", logo=" + logo + ", title=" + title + ", category=" + category + ", price=" + price
				+ ", authorId=" + authorId + ", publisher=" + publisher + ", publishedDate=" + publishedDate
				+ ", content=" + content + ", active=" + active + "]";
	}

}
