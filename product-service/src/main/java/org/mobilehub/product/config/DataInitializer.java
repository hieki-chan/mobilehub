package org.mobilehub.product.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.product.entity.*;
import org.mobilehub.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepo;

    @Override
    public void run(String... args) {
        // Prevent duplicate seeding on restart
        if (productRepo.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        List<Product> products = List.of(
                buildIphone15Pro(now),
                buildGalaxyS24Ultra(now),
                buildMacbookAirM3(now),
                buildDellXps13(now),
                buildRedmiNote13(now)
        );

        productRepo.saveAll(products);
    }

    // =========================
    // PRODUCT BUILDERS
    // =========================

    private Product buildIphone15Pro(LocalDateTime now) {
        Product product = baseProduct(
                "iPhone 15 Pro",
                "Apple flagship with A17 Pro, titanium frame, ProMotion display.",
                ProductStatus.ACTIVE
        );

        // Spec
        ProductSpec spec = new ProductSpec();
        spec.setOs("iOS 18");
        spec.setCpu("Apple A17 Pro");
        spec.setCpu_speed(3600);
        spec.setGpu("Apple 6-core GPU");
        spec.setBattery_cap(3274);
        spec.setRear_cam("48MP + 12MP + 12MP");
        spec.setFront_cam("12MP TrueDepth");
        spec.setScreen_res("2556x1179 OLED 120Hz");
        spec.setFeatures("Face ID, 5G, MagSafe");
        spec.setMaterial("Titanium frame, glass");
        spec.setSize_weight(187);
        spec.setBrand("Apple");
        spec.setRelease_date(now.minusMonths(2));
        attachSpec(product, spec);

        // Discount (active)
        ProductDiscount discount = new ProductDiscount();
        discount.setValueInPercent(5);
        discount.setStartDate(now.minusDays(1));
        discount.setEndDate(now.plusDays(10));
        attachDiscount(product, discount);

        // Variants
        ProductVariant natural256 = makeVariant("Natural Titanium", "#C8C7C2", 256, 8,
                BigDecimal.valueOf(29_990_000L));
        addImages(natural256,
                img(natural256, "iphone15pro/natural_1", "https://cdn.mobilehub.dev/products/iphone15pro/natural_1.jpg"),
                img(natural256, "iphone15pro/natural_2", "https://cdn.mobilehub.dev/products/iphone15pro/natural_2.jpg"),
                img(natural256, "iphone15pro/natural_3", "https://cdn.mobilehub.dev/products/iphone15pro/natural_3.jpg")
        );

        ProductVariant blue512 = makeVariant("Blue Titanium", "#5C6B7D", 512, 8,
                BigDecimal.valueOf(35_990_000L));
        addImages(blue512,
                img(blue512, "iphone15pro/blue_1", "https://cdn.mobilehub.dev/products/iphone15pro/blue_1.jpg"),
                img(blue512, "iphone15pro/blue_2", "https://cdn.mobilehub.dev/products/iphone15pro/blue_2.jpg"),
                img(blue512, "iphone15pro/blue_3", "https://cdn.mobilehub.dev/products/iphone15pro/blue_3.jpg")
        );

        attachVariants(product, natural256, blue512);
        product.setDefaultVariant(natural256);

        return product;
    }

    private Product buildGalaxyS24Ultra(LocalDateTime now) {
        Product product = baseProduct(
                "Samsung Galaxy S24 Ultra",
                "Top Android flagship with S-Pen and 200MP camera.",
                ProductStatus.ACTIVE
        );

        ProductSpec spec = new ProductSpec();
        spec.setOs("Android 14 / One UI");
        spec.setCpu("Snapdragon 8 Gen 3");
        spec.setCpu_speed(3300);
        spec.setGpu("Adreno 750");
        spec.setBattery_cap(5000);
        spec.setRear_cam("200MP + 50MP + 12MP + 10MP");
        spec.setFront_cam("12MP");
        spec.setScreen_res("3120x1440 AMOLED 120Hz");
        spec.setFeatures("S-Pen, 5G, IP68");
        spec.setMaterial("Armor Aluminum, glass");
        spec.setSize_weight(233);
        spec.setBrand("Samsung");
        spec.setRelease_date(now.minusMonths(9));
        attachSpec(product, spec);

        ProductDiscount discount = new ProductDiscount();
        discount.setValueInPercent(10);
        discount.setStartDate(now.minusDays(2));
        discount.setEndDate(now.plusDays(5));
        attachDiscount(product, discount);

        ProductVariant gray256 = makeVariant("Titanium Gray", "#555555", 256, 12,
                BigDecimal.valueOf(28_990_000L));
        addImages(gray256,
                img(gray256, "s24ultra/gray_1", "https://cdn.mobilehub.dev/products/s24ultra/gray_1.jpg"),
                img(gray256, "s24ultra/gray_2", "https://cdn.mobilehub.dev/products/s24ultra/gray_2.jpg")
        );

        ProductVariant violet512 = makeVariant("Titanium Violet", "#6C4C77", 512, 12,
                BigDecimal.valueOf(31_990_000L));
        addImages(violet512,
                img(violet512, "s24ultra/violet_1", "https://cdn.mobilehub.dev/products/s24ultra/violet_1.jpg"),
                img(violet512, "s24ultra/violet_2", "https://cdn.mobilehub.dev/products/s24ultra/violet_2.jpg")
        );

        attachVariants(product, gray256, violet512);
        product.setDefaultVariant(gray256);

        return product;
    }

    private Product buildMacbookAirM3(LocalDateTime now) {
        Product product = baseProduct(
                "MacBook Air 13 M3",
                "Ultralight laptop with Apple M3 and long battery life.",
                ProductStatus.COMING_SOON
        );

        ProductSpec spec = new ProductSpec();
        spec.setOs("macOS 15");
        spec.setCpu("Apple M3");
        spec.setCpu_speed(4050);
        spec.setGpu("Apple 10-core GPU");
        spec.setBattery_cap(52); // Wh-equivalent for demo
        spec.setRear_cam("N/A");
        spec.setFront_cam("1080p FaceTime HD");
        spec.setScreen_res("2560x1664 Liquid Retina");
        spec.setFeatures("MagSafe, Touch ID");
        spec.setMaterial("Aluminum unibody");
        spec.setSize_weight(1240);
        spec.setBrand("Apple");
        spec.setRelease_date(now.plusMonths(1));
        attachSpec(product, spec);

        ProductVariant silver256 = makeVariant("Silver", "#D9D9D9", 256, 8,
                BigDecimal.valueOf(27_990_000L));
        addImages(silver256,
                img(silver256, "mba_m3/silver_1", "https://cdn.mobilehub.dev/products/mba_m3/silver_1.jpg")
        );

        ProductVariant midnight512 = makeVariant("Midnight", "#0D1B2A", 512, 16,
                BigDecimal.valueOf(33_990_000L));
        addImages(midnight512,
                img(midnight512, "mba_m3/midnight_1", "https://cdn.mobilehub.dev/products/mba_m3/midnight_1.jpg")
        );

        attachVariants(product, silver256, midnight512);
        product.setDefaultVariant(silver256);

        return product;
    }

    private Product buildDellXps13(LocalDateTime now) {
        Product product = baseProduct(
                "Dell XPS 13 (2025)",
                "Premium Windows ultrabook with OLED display.",
                ProductStatus.ACTIVE
        );

        ProductSpec spec = new ProductSpec();
        spec.setOs("Windows 11");
        spec.setCpu("Intel Core Ultra 7 155H");
        spec.setCpu_speed(4100);
        spec.setGpu("Intel Arc");
        spec.setBattery_cap(55);
        spec.setRear_cam("N/A");
        spec.setFront_cam("FHD IR webcam");
        spec.setScreen_res("2880x1800 OLED");
        spec.setFeatures("Thunderbolt 4, Wi-Fi 7");
        spec.setMaterial("CNC aluminum");
        spec.setSize_weight(1190);
        spec.setBrand("Dell");
        spec.setRelease_date(now.minusMonths(6));
        attachSpec(product, spec);

        ProductDiscount discount = new ProductDiscount();
        discount.setValueInPercent(7);
        discount.setStartDate(now.minusDays(10));
        discount.setEndDate(now.plusDays(20));
        attachDiscount(product, discount);

        ProductVariant platinum512 = makeVariant("Platinum", "#C0C0C0", 512, 16,
                BigDecimal.valueOf(42_990_000L));
        addImages(platinum512,
                img(platinum512, "xps13/platinum_1", "https://cdn.mobilehub.dev/products/xps13/platinum_1.jpg"),
                img(platinum512, "xps13/platinum_2", "https://cdn.mobilehub.dev/products/xps13/platinum_2.jpg")
        );

        attachVariants(product, platinum512);
        product.setDefaultVariant(platinum512);

        return product;
    }

    private Product buildRedmiNote13(LocalDateTime now) {
        Product product = baseProduct(
                "Xiaomi Redmi Note 13",
                "Affordable AMOLED phone with 108MP camera.",
                ProductStatus.ACTIVE
        );

        ProductSpec spec = new ProductSpec();
        spec.setOs("Android 13 / MIUI");
        spec.setCpu("Snapdragon 685");
        spec.setCpu_speed(2800);
        spec.setGpu("Adreno 610");
        spec.setBattery_cap(5000);
        spec.setRear_cam("108MP + 8MP + 2MP");
        spec.setFront_cam("16MP");
        spec.setScreen_res("2400x1080 AMOLED 120Hz");
        spec.setFeatures("33W fast charge, NFC");
        spec.setMaterial("Plastic frame, glass");
        spec.setSize_weight(188);
        spec.setBrand("Xiaomi");
        spec.setRelease_date(now.minusMonths(12));
        attachSpec(product, spec);

        ProductVariant black128 = makeVariant("Black", "#000000", 128, 6,
                BigDecimal.valueOf(4_990_000L));
        addImages(black128,
                img(black128, "note13/black_1", "https://cdn.mobilehub.dev/products/note13/black_1.jpg")
        );

        ProductVariant blue256 = makeVariant("Blue", "#2E5AAC", 256, 8,
                BigDecimal.valueOf(5_990_000L));
        addImages(blue256,
                img(blue256, "note13/blue_1", "https://cdn.mobilehub.dev/products/note13/blue_1.jpg")
        );

        attachVariants(product, black128, blue256);
        product.setDefaultVariant(black128);

        return product;
    }

    // =========================
    // HELPERS (clean & reusable)
    // =========================

    private Product baseProduct(String name, String description, ProductStatus status) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setStatus(status);
        return p;
    }

    private void attachSpec(Product product, ProductSpec spec) {
        product.setSpec(spec);
        spec.setProduct(product);
    }

    private void attachDiscount(Product product, ProductDiscount discount) {
        product.setDiscount(discount);
        discount.setProduct(product);
    }

    private void attachVariants(Product product, ProductVariant... variants) {
        for (ProductVariant v : variants) {
            v.setProduct(product);
            product.getVariants().add(v);
        }
    }

    private ProductVariant makeVariant(String label, String hex, int storage, int ram, BigDecimal price) {
        ProductVariant v = new ProductVariant();
        v.setColor_label(label);
        v.setColor_hex(hex);
        v.setStorage_cap(storage);
        v.setRam(ram);
        v.setPrice(price);
        return v;
    }

    private ProductImage img(ProductVariant variant, String publicId, String url) {
        ProductImage image = ProductImage.builder()
                .publicId(publicId)
                .imageUrl(url)
                .status(ImageStatus.UPLOADED)
                .variant(variant)
                .build();
        return image;
    }

    private void addImages(ProductVariant variant, ProductImage... images) {
        if (images == null || images.length == 0) return;

        // add all images
        for (ProductImage img : images) {
            variant.getImages().add(img);
        }

        // set key image as first image
        variant.setKeyImage(images[0]);
    }
}
