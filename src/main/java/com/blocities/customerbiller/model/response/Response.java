package com.blocities.customerbiller.model.response;

import lombok.Data;

/**
 * Response model class.
 *
 * @author Lukman Olalekan
 */
@Data
public class Response<T> {
    private String status;
    private String message;
    private T data;
}
