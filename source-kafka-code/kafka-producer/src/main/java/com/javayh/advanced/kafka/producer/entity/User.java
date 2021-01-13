package com.javayh.advanced.kafka.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String pwd;
}
