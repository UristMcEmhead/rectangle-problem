package ru.itis.kpfu.rectangleproblem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.kpfu.rectangleproblem.config.AlgorithmProperties;
import ru.itis.kpfu.rectangleproblem.config.ShutdownManager;
import ru.itis.kpfu.rectangleproblem.model.Coordinate;
import ru.itis.kpfu.rectangleproblem.model.LRP;
import ru.itis.kpfu.rectangleproblem.model.Point;
import ru.itis.kpfu.rectangleproblem.model.enumerated.Orientation;
import ru.itis.kpfu.rectangleproblem.repository.LRPRepository;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class LRPService {

    private final LRPRepository lrpRepository;
    private final RectangleService rectangleService;
    private final ScrapService scrapService;
    private final GeometryFactory geometryFactory;
    private final AtomicLong step = new AtomicLong();
    private final AlgorithmProperties properties;
    private final ShutdownManager shutdownManager;

    private Double size;


    @Transactional
    public void initLRP() {
        LRP lrp = new LRP();
        this.size = 1 / properties.getSize();

        lrp.setStep(step.get());
        lrp.setHeight(this.size);
        lrp.setWidth(this.size);

        lrpRepository.save(lrp);
    }

    public LRP getCurrent() {
        return lrpRepository.findFirstByOrderByStepDesc();
    }

    @Transactional
    public void cropLRP(Long index) {
        LRP current = lrpRepository.findFirstByOrderByStepDesc();
        LRP newLRP = new LRP();

        double rectangleHeight = 1 / (properties.getSize() * properties.getSize() + index - 1);
        double extendedRectangleHeight = rectangleHeight + Math.pow(rectangleHeight, properties.getPower());
        Point scrapBottomLeft;
        Point scrapUpperRight;
        Orientation orientation;

        //Режем справа
        if (current.getHeight() < current.getWidth()) {
            double newLRPWidth = current.getWidth() - extendedRectangleHeight;
            orientation = Orientation.VERTICAL;
            newLRP.setWidth(newLRPWidth);
            newLRP.setHeight(current.getHeight());

            scrapBottomLeft = geometryFactory.createPoint(new Coordinate(current.getWidth(), this.size - current.getHeight()));
            scrapUpperRight = geometryFactory.createPoint(new Coordinate(newLRPWidth, this.size));
            //Режем снизу
        } else {
            double newLRPHeight = current.getHeight() - extendedRectangleHeight;
            newLRP.setHeight(newLRPHeight);
            newLRP.setWidth(current.getWidth());
            orientation = Orientation.HORIZONTAL;

            scrapBottomLeft = geometryFactory.createPoint(new Coordinate(0, this.size - current.getHeight()));
            scrapUpperRight = geometryFactory.createPoint(new Coordinate(current.getWidth(), this.size - newLRPHeight));
        }

        if (scrapBottomLeft.getX() < 0){
            log.info("Getting out of bounds on {}", index);
            shutdownManager.initiateShutdown(-1);
        }

        newLRP.setStep(step.incrementAndGet());
        newLRP.setRectangleIndex(index);

        scrapService.cropScrap(scrapBottomLeft, scrapUpperRight, orientation, false, false);
        lrpRepository.save(newLRP);
    }

    @Transactional
    public void cropLRP(){
        cropLRP(rectangleService.getStep().get());
    }

}
