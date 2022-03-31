package ru.itis.kpfu.rectangleproblem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rectangle extends RectangularWithPolygon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long index;

    @ManyToOne
    @JoinColumn(name = "scrap_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("rectangles")
    private Scrap scrap;
}
