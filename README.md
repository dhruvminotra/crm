# CRM

Veyora CRM backend — the product-admin services platform. The first module is the
**Hotel Extranet**, a re-build of the tf-main extranet (`HotelNavigation` / `HotelDataBean`)
on the new stack.

## Stack

- Java 17 (LTS), Spring Boot 3.3, Maven
- PostgreSQL + Flyway versioned migrations (same style as intl `flyway/pgsql`)
- Spring Data JPA, Spring Security (JWT), springdoc-openapi
- Layout follows the new service pattern: `controller -> service -> repository -> entity`

## Naming

Entity and service naming follows tf-main:

| tf-main | crm |
|---|---|
| `MarketPlaceHotel` | `entity/MarketPlaceHotel` |
| `HotelRoom` | `entity/HotelRoom` |
| `HotelSupplierMap` | `entity/HotelSupplierMap` |
| `SupplierPackagePricing` (PACKAGE_PRICING) | `entity/SupplierPackagePricing` |
| `SupplierPackagePricing` (RATE_PLAN) | `entity/RatePlan` |
| `PackageInventory` | `entity/PackageInventory` |
| `HotelPolicy` | `entity/HotelPolicy` |
| `ProductPromotionOffer` | `entity/ProductPromotionOffer` |
| `HotelDataBean` | `service/HotelDataService` |
| `PackageDataBean` (inventory) | `service/PackageDataService` |
| `SupplierPackageManager` | `service/SupplierPackageService` |

## API

Paths mirror the tf-main `/hotels/<action>` URLs under `/crm/api/v1`:

| tf-main | crm endpoint |
|---|---|
| `/hotels/manage-inventory` + `inventory-x` | `GET /api/v1/hotels/manage-inventory` |
| `/hotels/update-inventory` | `POST /api/v1/hotels/update-inventory` |
| `/hotels/update-rates` | `POST /api/v1/hotels/update-rates` |
| `/hotels/rate-plans` | `GET /api/v1/hotels/rate-plans` |
| `/hotels/edit-rate-plans` | `POST /api/v1/hotels/edit-rate-plans` |
| `/hotels/edit-rate-plan` | `GET /api/v1/hotels/edit-rate-plan?rpid=` |
| `/hotels/promotions`, `-save`, `-edit` | same names under `/api/v1/hotels` |
| `/hotels/discounts` | `GET /api/v1/hotels/discounts` |
| `/hotels/policies`, `-save`, `-edit` | same names under `/api/v1/hotels` |
| `/hotels/commissions`, `-save` | same names under `/api/v1/hotels` |
| `/hotels/hotel-contracts-manage` | `GET /api/v1/hotels/hotel-contracts-manage` |
| `/hotels/contract-x` | `POST /api/v1/hotels/contract-x` |
| `/hotels/inventory-z` | `GET /api/v1/hotels/inventory-z` |

Role gating matches tf-main `HotelAction` (e.g. commissions exclude HOTELIER).

Admin (hotel master, rooms, users, supplier mapping): `/api/v1/admin/**`.
Auth: `POST /api/v1/auth/login` -> JWT bearer token.

## Local setup

```sql
CREATE DATABASE crm;
CREATE ROLE crm_app WITH LOGIN PASSWORD 'crm_app_local';
GRANT ALL PRIVILEGES ON DATABASE crm TO crm_app;
```

```bash
mvn spring-boot:run
```

Server: `http://localhost:8082/crm` — Swagger: `/crm/swagger-ui.html`.

Seed users (local): `admin@veyora.com` / `admin123` (ADMIN),
`hotelier@veyora.com` / `admin123` (HOTELIER).

## Frontend

React UI lives in the separate `crm-ui` repo.
