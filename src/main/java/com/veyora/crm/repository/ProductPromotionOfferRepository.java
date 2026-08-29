package com.veyora.crm.repository;

import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import com.veyora.crm.entity.ProductPromotionOffer;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductPromotionOfferRepository extends JpaRepository<ProductPromotionOffer, Long> {

    List<ProductPromotionOffer> findByHotelIdAndTypeAndStatusIn(Long hotelId, ProductPromotionOfferType type,
            List<ProductPromotionOfferStatus> statuses);

    @Query("select p from ProductPromotionOffer p where p.hotelId in :hotelIds and p.type = :type "
            + "and p.status = :status and p.travelStartDate <= :endDate and p.travelEndDate >= :startDate")
    List<ProductPromotionOffer> findActiveForHotels(@Param("hotelIds") List<Long> hotelIds,
                                        @Param("type") ProductPromotionOfferType type,
                                        @Param("status") ProductPromotionOfferStatus status,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
