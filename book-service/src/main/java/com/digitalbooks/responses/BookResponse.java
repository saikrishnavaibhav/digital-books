package com.digitalbooks.responses;

import java.util.Objects;

public class BookResponse {

	private Long id;

	private String logo;

	private String title;

	private String category;

	private Long price;

	private String authorName;

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

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String value) {
		this.category = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		this.title = value;
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

	@Override
	public int hashCode() {
		return Objects.hash(authorName, category, id, logo, price, title);
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
		return Objects.equals(authorName, other.authorName) && Objects.equals(category, other.category)
				&& Objects.equals(id, other.id) && Objects.equals(logo, other.logo)
				&& Objects.equals(price, other.price) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "BookResponse [id=" + id + ", logo=" + logo + ", title=" + title + ", category=" + category + ", price="
				+ price + ", authorName=" + authorName + "]";
	}

}
