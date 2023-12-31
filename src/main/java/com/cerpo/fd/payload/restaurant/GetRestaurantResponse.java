package com.cerpo.fd.payload.restaurant;

import com.cerpo.fd.model.retailer.category.Category;

import java.util.List;

public record GetRestaurantResponse(Integer restaurantId, String restaurantName, String imgUrl, RestaurantDetails restaurantDetails, List<Category> categories) {
}
