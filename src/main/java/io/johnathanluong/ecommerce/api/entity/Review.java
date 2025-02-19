package io.johnathanluong.ecommerce.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // user whenever i finish it
    // @ManyToOne(fetch = FetchType.Lazy)
    // @JoinColumn(name = "user_id", nullable = false)
    // private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reviewText; 

    @Column(length = 16)
    private String sentiment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Review(){}

    public Review(Long id, Product product, String reviewText, String sentiment) {
        this.id = id;
        this.product = product;
        this.reviewText = reviewText;
        this.sentiment = sentiment;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((reviewText == null) ? 0 : reviewText.hashCode());
        result = prime * result + ((sentiment == null) ? 0 : sentiment.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
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
        Review other = (Review) obj;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (reviewText == null) {
            if (other.reviewText != null)
                return false;
        } else if (!reviewText.equals(other.reviewText))
            return false;
        if (sentiment == null) {
            if (other.sentiment != null)
                return false;
        } else if (!sentiment.equals(other.sentiment))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
