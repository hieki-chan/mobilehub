package org.mobilehub.product_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.product_service.entity.ImageStatus;
import org.mobilehub.product_service.entity.Product;
import org.mobilehub.product_service.entity.ProductImage;
import org.mobilehub.product_service.entity.ProductSpec;
import org.mobilehub.product_service.entity.ProductStatus;
import org.mobilehub.product_service.entity.ProductVariant;
import org.mobilehub.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepo;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    /**
     * If true: ALWAYS delete products and seed new random products on every restart.
     */
    @Value("${app.seed.always-random:true}")
    private boolean alwaysRandom;

    /**
     * How many random products to create each run.
     */
    @Value("${app.seed.count:1000}")
    private int seedCount;

    /**
     * Folder that contains images used for seeding (jpg/png/webp...).
     */
    @Value("${app.seed.image-folder:src/main/resources/static/images/phones}")
    private String imageFolder;

    private final Random random = new Random();

    // ---------- Templates ----------
    private static final List<PhoneTemplate> TEMPLATES = List.of(
            new PhoneTemplate("iPhone 15 Pro Max", "Apple",
                    "Flagship iPhone with titanium design and powerful performance.",
                    "iOS 18", "Apple A17 Pro", 3600, "Apple 6-core GPU",
                    List.of("2796x1290 OLED 120Hz", "2556x1179 OLED 120Hz"),
                    List.of("12MP TrueDepth", "12MP"),
                    List.of("48MP + 12MP + 12MP", "48MP + 12MP"),
                    4100, 4700, 205, 240,
                    "Titanium frame, glass",
                    List.of("Face ID", "5G", "MagSafe", "IP68", "eSIM", "Wi-Fi 6E"),
                    44_990_000L
            ),
            new PhoneTemplate("Galaxy S24 Ultra", "Samsung",
                    "Premium Android flagship with S Pen and pro-grade cameras.",
                    "Android 14 / One UI", "Snapdragon 8 Gen 3", 3300, "Adreno 750",
                    List.of("3120x1440 AMOLED 120Hz", "2340x1080 AMOLED 120Hz"),
                    List.of("12MP", "10MP"),
                    List.of("200MP + 50MP + 12MP + 10MP", "200MP + 12MP + 10MP"),
                    4700, 5500, 220, 250,
                    "Armor Aluminum, glass",
                    List.of("S-Pen", "5G", "IP68", "NFC", "Wireless charge", "UWB"),
                    28_990_000L
            ),
            new PhoneTemplate("Xiaomi 14", "Xiaomi",
                    "Compact flagship with strong camera system and fast charging.",
                    "Android 14 / HyperOS", "Snapdragon 8 Gen 3", 3300, "Adreno 750",
                    List.of("2670x1200 AMOLED 120Hz", "2400x1080 AMOLED 120Hz"),
                    List.of("32MP", "20MP"),
                    List.of("50MP + 50MP + 50MP", "50MP + 50MP"),
                    4400, 5100, 175, 205,
                    "Aluminum frame, glass",
                    List.of("5G", "NFC", "90W fast charge", "IR blaster", "Stereo speakers"),
                    18_990_000L
            ),
            new PhoneTemplate("OPPO Find X7", "OPPO",
                    "High-end camera phone with premium display and charging.",
                    "Android 14 / ColorOS", "Dimensity 9300", 3200, "Immortalis-G720",
                    List.of("2780x1264 AMOLED 120Hz", "2412x1080 AMOLED 120Hz"),
                    List.of("32MP", "16MP"),
                    List.of("50MP triple", "50MP + 50MP + 12MP"),
                    4700, 5500, 190, 225,
                    "Aluminum frame, glass",
                    List.of("5G", "NFC", "100W fast charge", "IP68", "Stereo speakers"),
                    20_990_000L
            ),
            new PhoneTemplate("Google Pixel 8 Pro", "Google",
                    "AI-first camera flagship with clean Android and long updates.",
                    "Android 14", "Google Tensor", 2800, "Google GPU",
                    List.of("2992x1344 OLED 120Hz", "2400x1080 OLED 90Hz"),
                    List.of("10.5MP", "11MP"),
                    List.of("50MP + 48MP + 48MP", "50MP + 48MP"),
                    4700, 5200, 200, 230,
                    "Aluminum frame, glass",
                    List.of("5G", "NFC", "AI Camera", "Wireless charge", "IP68"),
                    25_990_000L
            ),
            new PhoneTemplate("Sony Xperia 1 VI", "Sony",
                    "Creator-focused flagship with pro video and display tuning.",
                    "Android 14", "Snapdragon 8 Gen 3", 3300, "Adreno 750",
                    List.of("FHD+ OLED 120Hz", "4K OLED 120Hz"),
                    List.of("12MP", "8MP"),
                    List.of("48MP + 12MP + 12MP", "48MP + 12MP"),
                    4700, 5200, 180, 210,
                    "Aluminum frame, glass",
                    List.of("5G", "NFC", "Pro video", "Hi-Res audio", "IP68"),
                    30_990_000L
            )
    );

    private static final List<ColorOption> COLORS = List.of(
            new ColorOption("Black", "#111111"),
            new ColorOption("White", "#F5F5F5"),
            new ColorOption("Silver", "#C0C0C0"),
            new ColorOption("Titanium Gray", "#555555"),
            new ColorOption("Natural Titanium", "#C8C7C2"),
            new ColorOption("Ocean Blue", "#1F4E79"),
            new ColorOption("Emerald Green", "#0B6B50"),
            new ColorOption("Sunset Orange", "#D97B39"),
            new ColorOption("Obsidian", "#2B2B2B")
    );

    private static final List<Integer> RAM_OPTIONS = List.of(8, 12, 16);
    private static final List<Integer> STORAGE_OPTIONS = List.of(128, 256, 512, 1024);

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("[Seed] Disabled (app.seed.enabled=false).");
            return;
        }

        if (alwaysRandom) {
            log.warn("[Seed] always-random=true → deleting all products and seeding {} random phones.", seedCount);
            productRepo.deleteAll();
        } else {
            if (productRepo.count() > 0) {
                log.info("[Seed] Products already exist → skip seeding.");
                return;
            }
        }

        List<String> imageUrls = loadImageUrlsFromFolder(imageFolder);
        if (imageUrls.isEmpty()) {
            log.warn("[Seed] No phone images found in folder: {} (after filter). Will use placeholder urls.", imageFolder);
        } else {
            log.info("[Seed] Loaded {} phone image(s) from {} (after filter).", imageUrls.size(), imageFolder);
        }

        LocalDateTime now = LocalDateTime.now();

        List<Product> phones = new ArrayList<>(seedCount);
        for (int i = 0; i < seedCount; i++) {
            phones.add(buildRandomPhone(now, imageUrls));
        }

        productRepo.saveAll(phones);
        log.info("[Seed] Seeded {} random phone products.", phones.size());
    }

    private Product buildRandomPhone(LocalDateTime now, List<String> imageUrls) {
        PhoneTemplate t = pickOne(TEMPLATES);

        int ram = pickOne(RAM_OPTIONS);
        int storage = pickOne(STORAGE_OPTIONS);
        ColorOption color = pickOne(COLORS);

        // Make name unique (avoid DB unique constraint issues)
        String name = t.model + " " + storage + "GB - " + color.label;

        int battery = randomBetween(t.batteryMin, t.batteryMax);
        int weight = randomBetween(t.weightMin, t.weightMax);
        String screenRes = pickOne(t.screenResOptions);
        String frontCam = pickOne(t.frontCamOptions);
        String rearCam = pickOne(t.rearCamOptions);
        String features = joinRandomDistinct(t.featurePool, 3);

        int releaseMonthsAgo = randomBetween(0, 24);

        long price = calcPrice(t.basePrice, storage, ram);

        Product product = baseProduct(name, t.description, ProductStatus.ACTIVE);

        ProductSpec spec = new ProductSpec();
        spec.setBrand(t.brand);
        spec.setOs(t.os);
        spec.setCpu(t.cpu);
        spec.setCpu_speed(t.cpuSpeed);
        spec.setGpu(t.gpu);
        spec.setScreen_res(screenRes);
        spec.setFront_cam(frontCam);
        spec.setRear_cam(rearCam);
        spec.setBattery_cap(battery);
        spec.setSize_weight(weight);
        spec.setMaterial(t.material);
        spec.setFeatures(features);
        spec.setRelease_date(now.minusMonths(releaseMonthsAgo));
        attachSpec(product, spec);

        ProductVariant variant = makeVariant(color.label, color.hex, storage, ram, BigDecimal.valueOf(price));
        attachVariants(product, variant);
        product.setDefaultVariant(variant);

        String basePublicId = slugify(
                t.brand + "-" + t.model + "-" + color.label + "-" + storage + "gb-" + random.nextInt(1_000_000)
        );

        // Images
        List<String> picked = pickRandomDistinct(imageUrls, 4, basePublicId);

        List<ProductImage> imgs = new ArrayList<>();
        for (int i = 0; i < picked.size(); i++) {
            String url = picked.get(i);
            imgs.add(img(variant, basePublicId + "_" + (i + 1), url));
        }
        addImages(variant, imgs.toArray(new ProductImage[0]));

        return product;
    }

    // ---------- Image loading ----------
    private List<String> loadImageUrlsFromFolder(String folder) {
        Path dir = Paths.get(folder);

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.walk(dir, 2)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> isImageFile(p.getFileName().toString()))
                    .filter(p -> isPhoneImageName(p.getFileName().toString())) // ✅ only phone-related filenames
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());

            List<String> urls = new ArrayList<>(files.size());
            for (Path p : files) {
                urls.add(toPublicUrl(p));
            }

            log.info("[Seed] Phone images after filter = {}", urls.size());
            return urls;
        } catch (IOException e) {
            log.warn("[Seed] Failed to read image folder: {}", folder, e);
            return List.of();
        }
    }

    private boolean isImageFile(String filename) {
        String f = filename.toLowerCase(Locale.ROOT);
        return f.endsWith(".jpg") || f.endsWith(".jpeg") || f.endsWith(".png") || f.endsWith(".webp") || f.endsWith(".gif");
    }

    // ✅ CÁCH 3: lọc theo tên file (phải đổi tên ảnh có keyword)
    private boolean isPhoneImageName(String filename) {
        String f = filename.toLowerCase(Locale.ROOT);
        return f.contains("phone")
                || f.contains("smartphone")
                || f.contains("iphone")
                || f.contains("samsung")
                || f.contains("xiaomi")
                || f.contains("oppo")
                || f.contains("vivo")
                || f.contains("pixel")
                || f.contains("sony")
                || f.contains("realme")
                || f.contains("huawei")
                || f.contains("oneplus")
                || f.contains("galaxy");
    }

    private String toPublicUrl(Path file) {
        String normalized = file.toAbsolutePath().toString().replace("\\", "/");
        int idx = normalized.lastIndexOf("/static/");
        if (idx >= 0) {
            // e.g. /images/phones/xxx.jpg
            return normalized.substring(idx + "/static".length());
        }
        return "/images/" + file.getFileName().toString();
    }

    private List<String> pickRandomDistinct(List<String> all, int count, String seedKey) {
        if (all == null || all.isEmpty()) {
            // fallback placeholder (random images, might not be phone-related)
            List<String> placeholders = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                placeholders.add("https://picsum.photos/seed/" + seedKey + "-" + (i + 1) + "/900/900");
            }
            return placeholders;
        }

        List<String> copy = new ArrayList<>(all);
        Collections.shuffle(copy, random);

        if (copy.size() >= count) return copy.subList(0, count);

        List<String> result = new ArrayList<>(copy);
        while (result.size() < count) {
            result.add(copy.get(random.nextInt(copy.size())));
        }
        return result;
    }

    // ---------- Helpers ----------
    private int randomBetween(int minInclusive, int maxInclusive) {
        if (maxInclusive <= minInclusive) return minInclusive;
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }

    private <T> T pickOne(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private String joinRandomDistinct(List<String> pool, int count) {
        if (pool == null || pool.isEmpty()) return "";
        List<String> copy = new ArrayList<>(pool);
        Collections.shuffle(copy, random);
        return copy.stream().limit(Math.min(count, copy.size())).collect(Collectors.joining(", "));
    }

    private long calcPrice(long base, int storage, int ram) {
        long storageExtra = switch (storage) {
            case 256 -> 2_000_000L;
            case 512 -> 5_000_000L;
            case 1024 -> 9_000_000L;
            default -> 0L; // 128
        };
        long ramExtra = (ram >= 16) ? 2_000_000L : (ram >= 12 ? 1_000_000L : 0L);

        double factor = 0.88 + (random.nextDouble() * 0.24);

        long price = Math.round((base + storageExtra + ramExtra) * factor);

        return (price / 10_000L) * 10_000L;
    }

    private String slugify(String input) {
        if (input == null) return "seed";
        String n = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        n = n.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return n.isBlank() ? "seed" : n;
    }

    // ---------- Entity builders ----------
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
        return ProductImage.builder()
                .publicId(publicId)
                .imageUrl(url)
                .status(ImageStatus.UPLOADED)
                .variant(variant)
                .build();
    }

    private void addImages(ProductVariant variant, ProductImage... images) {
        if (images == null || images.length == 0) return;

        for (ProductImage img : images) {
            variant.getImages().add(img);
        }

        variant.setKeyImage(images[0]);
    }

    // ---------- Small data classes ----------
    private static class PhoneTemplate {
        final String model;
        final String brand;
        final String description;
        final String os;
        final String cpu;
        final int cpuSpeed;
        final String gpu;
        final List<String> screenResOptions;
        final List<String> frontCamOptions;
        final List<String> rearCamOptions;
        final int batteryMin;
        final int batteryMax;
        final int weightMin;
        final int weightMax;
        final String material;
        final List<String> featurePool;
        final long basePrice;

        PhoneTemplate(String model, String brand, String description,
                      String os, String cpu, int cpuSpeed, String gpu,
                      List<String> screenResOptions,
                      List<String> frontCamOptions,
                      List<String> rearCamOptions,
                      int batteryMin, int batteryMax,
                      int weightMin, int weightMax,
                      String material,
                      List<String> featurePool,
                      long basePrice) {
            this.model = model;
            this.brand = brand;
            this.description = description;
            this.os = os;
            this.cpu = cpu;
            this.cpuSpeed = cpuSpeed;
            this.gpu = gpu;
            this.screenResOptions = screenResOptions;
            this.frontCamOptions = frontCamOptions;
            this.rearCamOptions = rearCamOptions;
            this.batteryMin = batteryMin;
            this.batteryMax = batteryMax;
            this.weightMin = weightMin;
            this.weightMax = weightMax;
            this.material = material;
            this.featurePool = featurePool;
            this.basePrice = basePrice;
        }
    }

    private static class ColorOption {
        final String label;
        final String hex;

        ColorOption(String label, String hex) {
            this.label = label;
            this.hex = hex;
        }
    }
}
