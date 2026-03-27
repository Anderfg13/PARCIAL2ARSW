package edu.eci.arsw.blueprints.dto;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import java.util.List;
import java.util.stream.Collectors;

public class BlueprintMapper {

    public static BlueprintDTO toDTO(Blueprint blueprint) {
        if (blueprint == null) return null;
        List<PointDTO> points = blueprint.getPoints().stream()
                .map(BlueprintMapper::toDTO)
                .collect(Collectors.toList());
        return new BlueprintDTO(
                blueprint.getAuthor(),
                blueprint.getName(),
                points
        );
    }


    public static Blueprint toEntity(BlueprintDTO dto) {
        if (dto == null) return null;
        List<Point> points = dto.getPoints().stream()
                .map(BlueprintMapper::toEntity)
                .collect(Collectors.toList());
        Blueprint blueprint = new Blueprint(dto.getAuthor(), dto.getName(), points);
        return blueprint;
    }


    public static PointDTO toDTO(Point point) {
        if (point == null) return null;
        return new PointDTO(point.x(), point.y());
    }


    public static Point toEntity(PointDTO dto) {
        if (dto == null) return null;
        return new Point(dto.getX(), dto.getY());
    }
}
