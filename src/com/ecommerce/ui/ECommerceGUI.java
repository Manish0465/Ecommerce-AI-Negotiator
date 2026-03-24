package com.ecommerce.ui;

import com.ecommerce.models.User;
import com.ecommerce.services.UserService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.CartService;
import com.ecommerce.services.OrderService;
import com.ecommerce.util.DatabaseConnection;
import com.ecommerce.models.Order;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;

public class ECommerceGUI extends JFrame {

    private UserService    userService;
    private ProductService productService;
    private CartService    cartService;
    private OrderService   orderService;

    private User currentUser;

    private CardLayout cardLayout;
    private JPanel     mainPanel;
    private JPanel     loginPanel, registerPanel, mainAppPanel;
    private JPanel     productPanel, cartPanel, ordersPanel, adminPanel, homeContentPanel;
    private JPanel     contentArea;
    private CardLayout contentCardLayout;

    private JScrollPane productScrollPane;
    private JScrollPane cartScrollPane;
    private JScrollPane ordersScrollPane;

    private JButton navHomeBtn, navProductsBtn, navCartBtn, navOrdersBtn;
    private JButton navWishlistBtn, navMoodBtn, navAlertsBtn, navAdminBtn, navLogoutBtn;

    // Home welcome label reference so we can update it
    private JLabel welcomeLabel;

    private final Map<Integer, Double> priceAlerts = new HashMap<>();
    private final Map<Integer, String> wishlist    = new LinkedHashMap<>();

    // Track first-time login per email using Java Preferences
    private static final Preferences prefs = Preferences.userNodeForPackage(ECommerceGUI.class);

    private static final Map<String, String[]> MOOD_MAP = new LinkedHashMap<>();
    static {
        MOOD_MAP.put("Gaming",     new String[]{"gaming", "keyboard", "mouse", "monitor"});
        MOOD_MAP.put("Work",       new String[]{"laptop", "monitor", "hub", "keyboard"});
        MOOD_MAP.put("Mobile",     new String[]{"iphone", "samsung", "galaxy", "case", "charger"});
        MOOD_MAP.put("Chill",      new String[]{"headphone", "watch", "charger"});
        MOOD_MAP.put("Everything", new String[]{});
    }

    private static final Map<String, String> IMAGE_URLS = new LinkedHashMap<>();
    static {
        IMAGE_URLS.put("laptop",    "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=220&h=140&fit=crop");
        IMAGE_URLS.put("iphone",    "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=220&h=140&fit=crop");
        IMAGE_URLS.put("samsung",   "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=220&h=140&fit=crop");
        IMAGE_URLS.put("galaxy",    "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=220&h=140&fit=crop");
        IMAGE_URLS.put("ipad",      "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=220&h=140&fit=crop");
        IMAGE_URLS.put("headphone", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=220&h=140&fit=crop");
        IMAGE_URLS.put("watch",     "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=220&h=140&fit=crop");
        IMAGE_URLS.put("keyboard",  "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=220&h=140&fit=crop");
        IMAGE_URLS.put("mouse",     "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=220&h=140&fit=crop");
        IMAGE_URLS.put("monitor",   "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=220&h=140&fit=crop");
        IMAGE_URLS.put("usb",       "https://images.unsplash.com/photo-1625895197185-efcec01cffe0?w=220&h=140&fit=crop");
        IMAGE_URLS.put("charger",   "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=220&h=140&fit=crop");
        IMAGE_URLS.put("case",      "https://images.unsplash.com/photo-1601593346740-925612772716?w=220&h=140&fit=crop");
    }

    public ECommerceGUI() {
        initializeServices();
        initializeUI();
        DatabaseConnection.testConnection();
        startPriceAlertDaemon();
    }

    private void initializeServices() {
        userService    = new UserService();
        productService = new ProductService();
        cartService    = new CartService();
        orderService   = new OrderService();
    }

    private void initializeUI() {
        setTitle("ShopEasy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        createLoginPanel();
        createRegisterPanel();
        createMainAppPanel();

        add(mainPanel);
        showLoginScreen();
    }

    // =========================================================================
    // Login
    // =========================================================================
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(15, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("ShopEasy");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(99, 179, 237));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel sub = new JLabel("Your Smarter Shopping Experience");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 120, 150));
        gbc.gridy = 1;
        loginPanel.add(sub, gbc);

        addFormFieldLabel(loginPanel, gbc, "Email", 2);
        JTextField emailField = new JTextField(22);
        styleField(emailField);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        loginPanel.add(emailField, gbc);

        addFormFieldLabel(loginPanel, gbc, "Password", 3);
        JPasswordField passField = new JPasswordField(22);
        styleField(passField);
        gbc.gridx = 1; gbc.gridy = 3;
        loginPanel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        styleActionButton(loginBtn, new Color(66, 153, 225));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        loginPanel.add(loginBtn, gbc);

        JButton regBtn = new JButton("Create Account");
        styleActionButton(regBtn, new Color(56, 178, 172));
        gbc.gridy = 5;
        loginPanel.add(regBtn, gbc);

        JLabel msg = new JLabel(" ");
        msg.setForeground(new Color(252, 129, 129));
        gbc.gridy = 6;
        loginPanel.add(msg, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass  = new String(passField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) { msg.setText("Please fill all fields"); return; }
            if (userService.loginUser(email, pass)) {
                currentUser = userService.getCurrentUser();
                emailField.setText(""); passField.setText(""); msg.setText(" ");
                showMainApp();
            } else {
                msg.setText("Invalid email or password");
            }
        });
        regBtn.addActionListener(e -> showRegisterScreen());
        mainPanel.add(loginPanel, "LOGIN");
    }

    // =========================================================================
    // Register
    // =========================================================================
    private void createRegisterPanel() {
        registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(new Color(15, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(99, 179, 237));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        registerPanel.add(title, gbc);

        String[] labels = {"Full Name", "Email", "Password", "Confirm Password"};
        JTextField[] fields = new JTextField[4];
        for (int i = 0; i < labels.length; i++) {
            addFormFieldLabel(registerPanel, gbc, labels[i], i + 1);
            fields[i] = (i >= 2) ? new JPasswordField(22) : new JTextField(22);
            styleField(fields[i]);
            gbc.gridx = 1; gbc.gridy = i + 1; gbc.gridwidth = 1;
            registerPanel.add(fields[i], gbc);
        }

        JButton regBtn = new JButton("Register");
        styleActionButton(regBtn, new Color(72, 187, 120));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        registerPanel.add(regBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        styleActionButton(backBtn, new Color(90, 90, 110));
        gbc.gridy = 6;
        registerPanel.add(backBtn, gbc);

        JLabel msg = new JLabel(" ");
        msg.setForeground(new Color(252, 129, 129));
        gbc.gridy = 7;
        registerPanel.add(msg, gbc);

        regBtn.addActionListener(e -> {
            String name  = fields[0].getText().trim();
            String email = fields[1].getText().trim();
            String pass  = new String(((JPasswordField) fields[2]).getPassword());
            String conf  = new String(((JPasswordField) fields[3]).getPassword());
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) { msg.setText("Please fill all fields"); return; }
            if (!pass.equals(conf)) { msg.setText("Passwords do not match"); return; }
            if (userService.registerUser(name, email, pass, "user")) {
                msg.setForeground(new Color(104, 211, 145));
                msg.setText("Account created! Please login.");
                // Mark as new user (first login)
                prefs.putBoolean("firstLogin_" + email, true);
                for (JTextField f : fields) f.setText("");
            } else {
                msg.setForeground(new Color(252, 129, 129));
                msg.setText("Failed to create account.");
            }
        });
        backBtn.addActionListener(e -> showLoginScreen());
        mainPanel.add(registerPanel, "REGISTER");
    }
        private void placeOrderSuccess(double amount, String paymentMethod) {
    int orderId = orderService.placeOrder(currentUser.getId());

    if (orderId != -1) {
        JOptionPane.showMessageDialog(this,
                "Order #" + orderId +
                "\nPayment: " + paymentMethod +
                "\nAmount: Rs." + String.format("%,.0f", amount),
                "Order Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

        loadCart();
        loadOrders();
    } else {
        JOptionPane.showMessageDialog(this,
                "Failed to place order.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

    // =========================================================================
    // Main App Shell
    // =========================================================================
    private void createMainAppPanel() {
        mainAppPanel = new JPanel(new BorderLayout());
        mainAppPanel.add(buildTopBar(), BorderLayout.NORTH);

        contentCardLayout = new CardLayout();
        contentArea       = new JPanel(contentCardLayout);

        buildHomePanel();
        buildProductPanel();
        buildCartPanel();
        buildOrdersPanel();
        buildAdminPanel();
        buildWishlistPanel();
        buildMoodPanel();
        buildPriceAlertPanel();

        mainAppPanel.add(contentArea, BorderLayout.CENTER);
        mainPanel.add(mainAppPanel, "MAIN_APP");
    }

    // =========================================================================
    // Top Bar
    // =========================================================================
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(13, 17, 30));
        topBar.setPreferredSize(new Dimension(0, 52));

        JLabel brand = new JLabel("   ShopEasy");
        brand.setFont(new Font("Arial", Font.BOLD, 18));
        brand.setForeground(new Color(99, 179, 237));
        brand.setBorder(new EmptyBorder(0, 14, 0, 0));
        topBar.add(brand, BorderLayout.WEST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 8));
        navPanel.setOpaque(false);

        navHomeBtn     = navBtn("Home",         new Color(55, 65, 81));
        navProductsBtn = navBtn("Products ",   new Color(37, 99, 235));
        navCartBtn     = navBtn("Cart",          new Color(37, 99, 235));
        navOrdersBtn   = navBtn("Orders ",      new Color(37, 99, 235));
        navWishlistBtn = navBtn("Wishlist",      new Color(219, 39, 119));
        navMoodBtn     = navBtn("Mood Shop",     new Color(124, 58, 237));
        navAlertsBtn   = navBtn("Price Alerts",  new Color(217, 119, 6));
        navAdminBtn    = navBtn("Admin ",       new Color(107, 114, 128));
        navAdminBtn.setVisible(false);

        navHomeBtn.addActionListener(e     -> showHome());
        navCartBtn.addActionListener(e     -> showCart());
        navWishlistBtn.addActionListener(e -> showWishlist());
        navMoodBtn.addActionListener(e     -> showMoodShop());
        navAlertsBtn.addActionListener(e   -> showPriceAlerts());

        navProductsBtn.addActionListener(e -> {
            JPopupMenu menu = styledPopup();
            menu.add(styledMenuItem("All Products", () -> showProductCatalog()));
            menu.add(styledMenuItem("Shop by Mood", () -> showMoodShop()));
            menu.show(navProductsBtn, 0, navProductsBtn.getHeight());
        });

        navOrdersBtn.addActionListener(e -> {
            JPopupMenu menu = styledPopup();
            menu.add(styledMenuItem("My Orders", () -> showOrders()));
            menu.add(styledMenuItem("View Cart",  () -> showCart()));
            menu.show(navOrdersBtn, 0, navOrdersBtn.getHeight());
        });

        navAdminBtn.addActionListener(e -> {
            JPopupMenu menu = styledPopup();
            menu.add(styledMenuItem("Dashboard", () -> showAdminPanel()));
            menu.show(navAdminBtn, 0, navAdminBtn.getHeight());
        });

        navPanel.add(navHomeBtn); navPanel.add(navProductsBtn); navPanel.add(navCartBtn);
        navPanel.add(navOrdersBtn); navPanel.add(navWishlistBtn); navPanel.add(navMoodBtn);
        navPanel.add(navAlertsBtn); navPanel.add(navAdminBtn);
        topBar.add(navPanel, BorderLayout.CENTER);

        navLogoutBtn = navBtn("Logout", new Color(185, 28, 28));
        navLogoutBtn.addActionListener(e -> logout());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        rightPanel.setOpaque(false);
        rightPanel.add(navLogoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPopupMenu styledPopup() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(20, 25, 45));
        menu.setBorder(BorderFactory.createLineBorder(new Color(50, 60, 100), 1));
        return menu;
    }

    private JMenuItem styledMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(new Color(20, 25, 45));
        item.setForeground(new Color(196, 210, 255));
        item.setFont(new Font("Arial", Font.PLAIN, 13));
        item.setBorder(new EmptyBorder(8, 14, 8, 14));
        item.addActionListener(e -> action.run());
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent ev) { item.setBackground(new Color(37, 99, 235)); }
            public void mouseExited(java.awt.event.MouseEvent ev)  { item.setBackground(new Color(20, 25, 45)); }
        });
        return item;
    }

    private JButton navBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(5, 11, 5, 11));
        b.setOpaque(true);
        return b;
    }

    // =========================================================================
    // Home — clickable feature cards + first/returning login greeting
    // =========================================================================
    private void buildHomePanel() {
        homeContentPanel = new JPanel(new GridBagLayout());
        homeContentPanel.setBackground(new Color(10, 12, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(8, 8, 8, 8);

        // Welcome label — updated on login
        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setName("welcomeLabel");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(new Color(99, 179, 237));
        gbc.gridy = 0; homeContentPanel.add(welcomeLabel, gbc);

        JLabel sub = new JLabel("India's first app with AI Price Negotiation · Mood Shopping · Price Drop Alerts");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 130, 160));
        gbc.gridy = 1; homeContentPanel.add(sub, gbc);

        // Clickable feature cards
        JPanel features = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        features.setOpaque(false);

        // Each card is clickable and navigates to its feature
        features.add(buildClickableFeatureCard("AI Negotiate",  "Haggle price down with AI",  () -> showProductCatalog()));
        features.add(buildClickableFeatureCard("Mood Shop",     "Get picks by your mood",      () -> showMoodShop()));
        features.add(buildClickableFeatureCard("Price Alerts",  "Notified when price drops",   () -> showPriceAlerts()));
        features.add(buildClickableFeatureCard("Wishlist",      "Share wishlists with friends", () -> showWishlist()));

        gbc.gridy = 2; gbc.insets = new Insets(20, 8, 8, 8);
        homeContentPanel.add(features, gbc);

        JButton shopBtn = new JButton("Browse All Products");
        styleActionButton(shopBtn, new Color(37, 99, 235));
        shopBtn.addActionListener(e -> showProductCatalog());
        gbc.gridy = 3; gbc.insets = new Insets(14, 8, 8, 8);
        homeContentPanel.add(shopBtn, gbc);

        contentArea.add(homeContentPanel, "HOME");
    }

    private JPanel buildClickableFeatureCard(String title, String desc, Runnable onClick) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 24, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                new EmptyBorder(16, 18, 16, 18)));
        card.setPreferredSize(new Dimension(180, 80));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 13));
        t.setForeground(new Color(196, 210, 255));
        t.setAlignmentX(CENTER_ALIGNMENT);

        JLabel d = new JLabel("<html><center>" + desc + "</center></html>");
        d.setFont(new Font("Arial", Font.PLAIN, 11));
        d.setForeground(new Color(100, 110, 140));
        d.setAlignmentX(CENTER_ALIGNMENT);

        card.add(t); card.add(Box.createRigidArea(new Dimension(0, 4))); card.add(d);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(30, 36, 60));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(99, 179, 237), 1),
                        new EmptyBorder(16, 18, 16, 18)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(20, 24, 40));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                        new EmptyBorder(16, 18, 16, 18)));
            }
            public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
        });

        return card;
    }

    // =========================================================================
    // Products
    // =========================================================================
    private void buildProductPanel() {
        productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(new Color(10, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Product Catalog");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(196, 210, 255));
        header.add(title, BorderLayout.WEST);
        productPanel.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new WrapLayout(FlowLayout.CENTER, 18, 18));
        grid.setBackground(new Color(10, 12, 20));
        grid.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setBackground(new Color(10, 12, 20));
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridx = 0; wgbc.gridy = 0; wgbc.weightx = 1; wgbc.weighty = 1;
        wgbc.fill = GridBagConstraints.BOTH;
        gridWrapper.add(grid, wgbc);

        productScrollPane = new JScrollPane(gridWrapper);
        productScrollPane.setBorder(null);
        productScrollPane.setBackground(new Color(10, 12, 20));
        productScrollPane.getViewport().setBackground(new Color(10, 12, 20));
        productScrollPane.getVerticalScrollBar().setUnitIncrement(22);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        productPanel.add(productScrollPane, BorderLayout.CENTER);
        contentArea.add(productPanel, "PRODUCTS");
    }

    private void loadProducts() { loadProductsFiltered(null); }

    private void loadProductsFiltered(String[] keywords) {
        JPanel gridWrapper = (JPanel) productScrollPane.getViewport().getView();
        JPanel grid = (JPanel) gridWrapper.getComponent(0);
        grid.removeAll();

        var products = productService.getAllProducts();
        if (products.isEmpty()) {
            JLabel lbl = new JLabel("No products available");
            lbl.setForeground(Color.GRAY); grid.add(lbl);
        } else {
            for (var p : products) {
                if (keywords == null || keywords.length == 0 || matchesKeywords(p.getName(), keywords))
                    grid.add(createProductCard(p));
            }
            if (grid.getComponentCount() == 0) {
                JLabel lbl = new JLabel("No products match this mood");
                lbl.setForeground(new Color(120, 130, 160)); grid.add(lbl);
            }
        }
        grid.revalidate(); grid.repaint();
    }

    private boolean matchesKeywords(String name, String[] keywords) {
        String lower = name.toLowerCase();
        for (String k : keywords) if (lower.contains(k)) return true;
        return false;
    }

    // =========================================================================
    // Product Card
    // =========================================================================
    private JPanel createProductCard(com.ecommerce.models.Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(240, 350));
        card.setBackground(new Color(18, 22, 38));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                new EmptyBorder(0, 0, 10, 0)));

        JLabel imgLbl = new JLabel();
        imgLbl.setPreferredSize(new Dimension(240, 150));
        imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imgLbl.setBackground(new Color(25, 30, 50)); imgLbl.setOpaque(true);
        new Thread(() -> {
            try {
                BufferedImage img = ImageIO.read(new URL(getImageUrl(product.getName())));
                Image scaled = img.getScaledInstance(240, 150, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> imgLbl.setIcon(new ImageIcon(scaled)));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    imgLbl.setText("[img]"); imgLbl.setForeground(new Color(60, 70, 100));
                });
            }
        }).start();
        card.add(imgLbl, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(18, 22, 38));
        info.setBorder(new EmptyBorder(10, 12, 6, 12));

        JLabel nameL  = new JLabel("<html><b>" + product.getName() + "</b></html>");
        nameL.setFont(new Font("Arial", Font.BOLD, 13)); nameL.setForeground(new Color(196, 210, 255));

        JLabel priceL = new JLabel("Rs. " + String.format("%,.0f", product.getPrice()));
        priceL.setFont(new Font("Arial", Font.BOLD, 17)); priceL.setForeground(new Color(52, 211, 153));

        JLabel stockL = new JLabel(product.getStock() > 0
                ? "In stock (" + product.getStock() + ")" : "Out of stock");
        stockL.setFont(new Font("Arial", Font.PLAIN, 11));
        stockL.setForeground(product.getStock() > 0 ? new Color(52, 211, 153) : new Color(248, 113, 113));

        info.add(nameL); info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(priceL); info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(stockL);
        card.add(info, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(1, 4, 4, 0));
        btns.setBackground(new Color(18, 22, 38));
        btns.setBorder(new EmptyBorder(0, 10, 0, 10));

        JButton addBtn       = miniBtn("Cart",      new Color(37, 99, 235));
        JButton negotiateBtn = miniBtn("Negotiate", new Color(124, 58, 237));
        JButton alertBtn     = miniBtn("Alert",     new Color(217, 119, 6));
        JButton wishBtn      = miniBtn("Wish",      new Color(219, 39, 119));

        addBtn.setToolTipText("Add to Cart");
        negotiateBtn.setToolTipText("AI Negotiate Price");
        alertBtn.setToolTipText("Set Price Drop Alert");
        wishBtn.setToolTipText("Add to Wishlist");

        addBtn.addActionListener(e -> {
            if (product.getStock() <= 0) { JOptionPane.showMessageDialog(this, "Out of stock!"); return; }
            boolean ok = cartService.addItemToCart(currentUser.getId(), product.getId(), 1);
            showToast(ok ? product.getName() + " added to cart!" : "Failed to add.", ok);
        });
        negotiateBtn.addActionListener(e -> openNegotiateDialog(product));
        alertBtn.addActionListener(e     -> openAlertDialog(product));
        wishBtn.addActionListener(e -> {
            wishlist.put(product.getId(), product.getName());
            showToast("Added " + product.getName() + " to wishlist!", true);
        });

        btns.add(addBtn); btns.add(negotiateBtn); btns.add(alertBtn); btns.add(wishBtn);
        card.add(btns, BorderLayout.SOUTH);
        return card;
    }

    // =========================================================================
    // FEATURE 1 — AI Price Negotiator (Advanced, smart discount logic)
    // =========================================================================
    private void openNegotiateDialog(com.ecommerce.models.Product product) {
        final double[] negotiatedPrice = { product.getPrice() };
        final boolean[] dealClosed   = { false };
        List<Map<String, String>> history = new ArrayList<>();

        // Generate a random max discount between 8% and 25% — different for each product/session
        Random rnd = new Random();
        // max discount is random between 8 and 25
        final int maxDiscountPct = 8 + rnd.nextInt(18);
        final double minPrice    = product.getPrice() * (1.0 - maxDiscountPct / 100.0);

        JDialog dialog = new JDialog(this, "AI Price Negotiator - " + product.getName(), true);
        dialog.setSize(580, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(12, 15, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 25, 45));
        header.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLbl = new JLabel("AI Price Negotiator");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(new Color(167, 139, 250));

        JLabel listedLbl = new JLabel("Listed: Rs." + String.format("%,.0f", product.getPrice()));
        listedLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        listedLbl.setForeground(new Color(52, 211, 153));

        header.add(titleLbl, BorderLayout.WEST);
        header.add(listedLbl, BorderLayout.EAST);
        dialog.add(header, BorderLayout.NORTH);

        // Chat area
        JPanel chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(new Color(12, 15, 25));
        chatArea.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(null);
        chatScroll.setBackground(new Color(12, 15, 25));
        chatScroll.getViewport().setBackground(new Color(12, 15, 25));
        chatScroll.getVerticalScrollBar().setUnitIncrement(12);
        dialog.add(chatScroll, BorderLayout.CENTER);

        // Opening message — does NOT reveal discount
        addChatBubble(chatArea, "Hello " + currentUser.getName() + "! I see you're interested in the "
                + product.getName() + " at Rs." + String.format("%,.0f", product.getPrice())
                + ". What can I do for you today?", false);

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(new Color(18, 22, 38));
        inputRow.setBorder(new EmptyBorder(10, 14, 10, 14));

        JTextField msgField = new JTextField();
        styleField(msgField);

        JButton sendBtn = new JButton("Send");
        styleActionButton(sendBtn, new Color(124, 58, 237));
        sendBtn.setPreferredSize(new Dimension(90, 38));

        // "Add to Cart at Deal Price" button — visible after deal
        JButton addDealBtn = new JButton("Add to Cart at Deal Price");
        styleActionButton(addDealBtn, new Color(52, 168, 83));
        addDealBtn.setVisible(false);
        addDealBtn.addActionListener(e -> {
    double dealPrice = negotiatedPrice[0];

    // ✅ Add to cart with negotiated price (NO product price change)
    boolean added = cartService.addItemWithCustomPrice(
            currentUser.getId(),
            product.getId(),
            1,
            dealPrice
    );

    if (added) {
        showToast("Added to cart at Rs." + String.format("%,.0f", dealPrice) + "!", true);
        loadCart();
        loadProducts(); // keeps product price original
    } else {
        showToast("Failed to add to cart.", false);
    }

    dialog.dispose();
});
        // Advanced system prompt — AI acts like a real smart shopkeeper
        String systemPrompt =
            "You are a smart, witty Indian shopkeeper AI negotiating price with a customer. "
            + "Product: " + product.getName() + ". "
            + "Listed price: Rs." + String.format("%.0f", product.getPrice()) + ". "
            + "You have a SECRET maximum discount of " + maxDiscountPct + "% (minimum price: Rs." + String.format("%.0f", minPrice) + "). "
            + "IMPORTANT RULES: "
            + "1. NEVER reveal the maximum discount percentage or minimum price to the customer. "
            + "2. Do NOT offer any discount unless the customer asks for one or makes an offer. If they just say hello or ask about the product, just describe it positively. "
            + "3. When a discount is asked, start by offering a SMALL discount (2-5%). Reveal more only if the customer pushes back. "
            + "4. Negotiate step by step — don't jump to maximum discount. Make the customer feel they earned it. "
            + "5. If the customer's offer is below your minimum price, firmly but politely decline and counter-offer. "
            + "6. If the customer's offer is at or above the minimum price, you can ACCEPT. "
            + "7. When accepting, your reply MUST contain exactly this on its own line: DEAL_ACCEPTED:<number> (example: DEAL_ACCEPTED:54000). No Rs symbol, no commas, just the number. "
            + "8. Only accept a deal if the customer clearly says yes to YOUR counter-offer (e.g., 'ok', 'deal', 'agreed', 'done', 'fine', 'yes'). "
            + "9. If customer says 'no deal', 'not interested', 'forget it', continue negotiating or ask what price works for them. "
            + "10. Keep replies short (1-3 sentences). Be friendly and a bit playful like a real shopkeeper. "
            + "11. You can mention things like festival season, last piece, popular item to justify why you can't go lower. "
            + "12. Never break character. You are always the shopkeeper, not an AI.";
            
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            history.add(systemMsg);

        Runnable sendMessage = () -> {
            if (dealClosed[0]) return;

            String userMsg = msgField.getText().trim();
            if (userMsg.isEmpty()) return;
            msgField.setText("");

            addChatBubble(chatArea, userMsg, true);
            chatArea.revalidate(); chatArea.repaint();
            scrollToBottom(chatScroll);

            JLabel typing = new JLabel("  AI is thinking...");
            typing.setFont(new Font("Arial", Font.ITALIC, 12));
            typing.setForeground(new Color(100, 110, 150));
            typing.setAlignmentX(LEFT_ALIGNMENT);
            chatArea.add(typing); chatArea.revalidate(); chatArea.repaint();

            Map<String, String> userTurn = new HashMap<>();
            userTurn.put("role", "user"); userTurn.put("content", userMsg);
            history.add(userTurn);

            new Thread(() -> {
                String reply = callNegotiatorAPI(systemPrompt, history);

                SwingUtilities.invokeLater(() -> {
                    chatArea.remove(typing);

                    // Check for DEAL_ACCEPTED:<price> token
                    java.util.regex.Matcher dealMatcher = java.util.regex.Pattern
                            .compile("DEAL_ACCEPTED:(\\d+(?:\\.\\d+)?)")
                            .matcher(reply);

                    if (dealMatcher.find()) {
                        double finalPrice = Double.parseDouble(dealMatcher.group(1));
                        negotiatedPrice[0] = finalPrice;
                        dealClosed[0] = true;

                        // Remove the raw token from displayed message
                        String cleanReply = reply.replaceAll("DEAL_ACCEPTED:\\d+(\\.\\d+)?", "").trim();
                        if (cleanReply.isEmpty())
                            cleanReply = "Deal! Final price: Rs." + String.format("%,.0f", finalPrice)
                                    + ". Pleasure doing business with you!";
                        addChatBubble(chatArea, cleanReply, false);

                        // Show deal summary banner
                        JPanel dealBanner = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        dealBanner.setBackground(new Color(6, 40, 20));
                        dealBanner.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(52, 211, 153), 1),
                                new EmptyBorder(6, 12, 6, 12)));
                        double saved = product.getPrice() - finalPrice;
                        double pct   = (saved / product.getPrice()) * 100;
                        JLabel dealInfo = new JLabel(String.format(
                                "Deal Price: Rs.%,.0f   |   You saved Rs.%,.0f  (%.0f%% off)",
                                finalPrice, saved, pct));
                        dealInfo.setFont(new Font("Arial", Font.BOLD, 13));
                        dealInfo.setForeground(new Color(52, 211, 153));
                        dealBanner.add(dealInfo);
                        dealBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        dealBanner.setAlignmentX(LEFT_ALIGNMENT);
                        chatArea.add(dealBanner);

                        msgField.setEnabled(false);
                        sendBtn.setEnabled(false);
                        addDealBtn.setVisible(true);

                        showToast("Deal at Rs." + String.format("%,.0f", finalPrice) + "!", true);
                    } else {
                        addChatBubble(chatArea, reply, false);
                    }

                    Map<String, String> aiTurn = new HashMap<>();
                    aiTurn.put("role", "assistant"); aiTurn.put("content", reply);
                    history.add(aiTurn);

                    chatArea.revalidate(); chatArea.repaint();
                    scrollToBottom(chatScroll);
                });
            }).start();
        };

        sendBtn.addActionListener(e -> sendMessage.run());
        msgField.addActionListener(e -> sendMessage.run());
        // ===== FIX: ADD INPUT + BUTTON UI =====
        inputRow.add(msgField, BorderLayout.CENTER);
        inputRow.add(sendBtn, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout(0, 6));
        southPanel.setBackground(new Color(18, 22, 38));
        southPanel.add(inputRow, BorderLayout.NORTH);
        southPanel.add(addDealBtn, BorderLayout.SOUTH);

        // 🔥 MOST IMPORTANT LINE
        dialog.add(southPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);}

    private String callNegotiatorAPI(String systemPrompt, List<Map<String, String>> history) {
        try {
            StringBuilder messagesJson = new StringBuilder("[");
            messagesJson.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"}");
            for (Map<String, String> turn : history) {
                messagesJson.append(",{\"role\":\"").append(turn.get("role"))
                        .append("\",\"content\":\"").append(escapeJson(turn.get("content"))).append("\"}");
            }
            messagesJson.append("]");

            String body = "{\"model\":\"openai/gpt-3.5-turbo\","
                    + "\"messages\":" + messagesJson + ","
                    + "\"max_tokens\":200,"
                    + "\"temperature\":0.75}";

            URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getApiKey());
            conn.setRequestProperty("HTTP-Referer", "http://localhost");
            conn.setRequestProperty("X-Title", "ShopEasy AI");
            conn.setDoOutput(true);
            conn.setConnectTimeout(12000);
            conn.setReadTimeout(20000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                String resp = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                int start = resp.indexOf("\"content\":\"") + 11;
                StringBuilder result = new StringBuilder();
                for (int i = start; i < resp.length(); i++) {
                    char c = resp.charAt(i);
                    if (c == '"' && resp.charAt(i - 1) != '\\') break;
                    result.append(c);
                }
                return result.toString().replace("\\n", "\n").replace("\\\"", "\"").replace("\\'", "'");
            } else {
                InputStream err = conn.getErrorStream();
                if (err != null) return "API Error " + conn.getResponseCode() + ": " + new String(err.readAllBytes(), StandardCharsets.UTF_8);
                return "API Error: " + conn.getResponseCode();
            }
        } catch (Exception ex) {
            return "Connection error: " + ex.getMessage();
        }
    }

    private void addChatBubble(JPanel container, String text, boolean isUser) {
        JPanel row = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel bubble = new JLabel("<html><div style='width:320px;padding:4px'>" + text + "</div></html>");
        bubble.setFont(new Font("Arial", Font.PLAIN, 13));
        bubble.setOpaque(true);
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12));
        bubble.setBackground(isUser ? new Color(37, 99, 235) : new Color(30, 35, 60));
        bubble.setForeground(isUser ? Color.WHITE : new Color(196, 210, 255));

        row.add(bubble);
        container.add(row);
        container.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private void scrollToBottom(JScrollPane sp) {
        SwingUtilities.invokeLater(() ->
                sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum()));
    }

    // =========================================================================
    // FEATURE 2 — Mood Shopping
    // =========================================================================
    private void buildMoodPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(10, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Mood-Based Shopping");
        title.setFont(new Font("Arial", Font.BOLD, 20)); title.setForeground(new Color(196, 210, 255));
        JLabel sub = new JLabel("Pick your mood and see matching products");
        sub.setForeground(new Color(100, 110, 150));
        header.add(title, BorderLayout.WEST); header.add(sub, BorderLayout.EAST);

        JPanel moodPicker = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        moodPicker.setBackground(new Color(20, 25, 45));
        moodPicker.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel resultGrid = new JPanel(new WrapLayout(FlowLayout.CENTER, 18, 18));
        resultGrid.setBackground(new Color(10, 12, 20));
        resultGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setBackground(new Color(10, 12, 20));
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridx = 0; wgbc.gridy = 0; wgbc.weightx = 1; wgbc.weighty = 1;
        wgbc.fill = GridBagConstraints.BOTH;
        gridWrapper.add(resultGrid, wgbc);

        JScrollPane resultScroll = new JScrollPane(gridWrapper);
        resultScroll.setBorder(null);
        resultScroll.getViewport().setBackground(new Color(10, 12, 20));
        resultScroll.getVerticalScrollBar().setUnitIncrement(22);
        resultScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        for (Map.Entry<String, String[]> entry : MOOD_MAP.entrySet()) {
            JButton btn = new JButton(entry.getKey());
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setBackground(new Color(30, 35, 60)); btn.setForeground(new Color(196, 210, 255));
            btn.setFocusPainted(false); btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(10, 22, 10, 22)); btn.setOpaque(true);

            btn.addActionListener(e -> {
                for (Component c : moodPicker.getComponents())
                    if (c instanceof JButton) ((JButton) c).setBackground(new Color(30, 35, 60));
                btn.setBackground(new Color(99, 60, 180));

                resultGrid.removeAll();
                String[] kws = entry.getValue();
                var products = productService.getAllProducts();
                for (var p : products)
                    if (kws.length == 0 || matchesKeywords(p.getName(), kws))
                        resultGrid.add(createProductCard(p));
                if (resultGrid.getComponentCount() == 0) {
                    JLabel lbl = new JLabel("No products match this mood");
                    lbl.setForeground(new Color(120, 130, 160)); resultGrid.add(lbl);
                }
                resultGrid.revalidate(); resultGrid.repaint();
            });
            moodPicker.add(btn);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.NORTH); top.add(moodPicker, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH); panel.add(resultScroll, BorderLayout.CENTER);
        contentArea.add(panel, "MOOD");
    }

    // =========================================================================
    // FEATURE 3 — Price Drop Alerts
    // =========================================================================
    private void openAlertDialog(com.ecommerce.models.Product product) {
        String input = JOptionPane.showInputDialog(this,
                "<html><b>Set Price Drop Alert</b><br>"
                + "Current price: Rs." + String.format("%,.0f", product.getPrice()) + "<br>"
                + "Notify me when price drops to (Rs.):</html>",
                "Price Alert - " + product.getName(), JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        try {
            double target = Double.parseDouble(input.trim().replace(",", ""));
            if (target >= product.getPrice()) {
                JOptionPane.showMessageDialog(this, "Target must be lower than current price!", "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            priceAlerts.put(product.getId(), target);
            showToast("Alert set for " + product.getName() + " at Rs." + String.format("%,.0f", target), true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildPriceAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(10, 12, 20));
        panel.setName("priceAlertPanel");

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("My Price Drop Alerts");
        title.setFont(new Font("Arial", Font.BOLD, 20)); title.setForeground(new Color(196, 210, 255));
        header.add(title, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(10, 12, 20));
        listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        listPanel.setName("alertList");

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null); sp.getViewport().setBackground(new Color(10, 12, 20));
        panel.add(sp, BorderLayout.CENTER);
        contentArea.add(panel, "ALERTS");
    }

    private void refreshAlertPanel() {
        for (Component c : contentArea.getComponents()) {
            if (!(c instanceof JPanel) || !"priceAlertPanel".equals(c.getName())) continue;
            JScrollPane sp = (JScrollPane) ((JPanel) c).getComponent(1);
            JPanel list = (JPanel) sp.getViewport().getView();
            list.removeAll();

            if (priceAlerts.isEmpty()) {
                JLabel lbl = new JLabel("No alerts set. Click Alert on any product card.");
                lbl.setForeground(new Color(100, 110, 150)); lbl.setFont(new Font("Arial", Font.ITALIC, 13));
                lbl.setAlignmentX(CENTER_ALIGNMENT);
                list.add(Box.createRigidArea(new Dimension(0, 20))); list.add(lbl);
            } else {
                var products = productService.getAllProducts();
                for (Map.Entry<Integer, Double> alert : priceAlerts.entrySet()) {
                    products.stream().filter(p -> p.getId() == alert.getKey()).findFirst().ifPresent(p -> {
                        JPanel row = new JPanel(new BorderLayout(12, 0));
                        row.setBackground(new Color(18, 22, 38));
                        row.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                                new EmptyBorder(12, 16, 12, 16)));
                        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                        JLabel nameL = new JLabel(p.getName());
                        nameL.setFont(new Font("Arial", Font.BOLD, 13)); nameL.setForeground(new Color(196, 210, 255));

                        JLabel prices = new JLabel("Current: Rs." + String.format("%,.0f", p.getPrice())
                                + "   ->   Alert at: Rs." + String.format("%,.0f", alert.getValue()));
                        prices.setForeground(new Color(217, 119, 6));

                        JButton del = miniBtn("Remove", new Color(185, 28, 28));
                        del.addActionListener(ev -> { priceAlerts.remove(alert.getKey()); refreshAlertPanel(); });

                        row.add(nameL, BorderLayout.WEST);
                        row.add(prices, BorderLayout.CENTER);
                        row.add(del, BorderLayout.EAST);
                        list.add(row); list.add(Box.createRigidArea(new Dimension(0, 8)));
                    });
                }
            }
            list.revalidate(); list.repaint(); break;
        }
    }

    private void startPriceAlertDaemon() {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                if (priceAlerts.isEmpty() || currentUser == null) return;
                var products = productService.getAllProducts();
                for (Map.Entry<Integer, Double> alert : new HashMap<>(priceAlerts).entrySet()) {
                    products.stream()
                            .filter(p -> p.getId() == alert.getKey() && p.getPrice() <= alert.getValue())
                            .findFirst().ifPresent(p -> SwingUtilities.invokeLater(() -> {
                                priceAlerts.remove(alert.getKey());
                                JOptionPane.showMessageDialog(ECommerceGUI.this,
                                        "Price Drop! " + p.getName() + " is now Rs."
                                        + String.format("%,.0f", p.getPrice()),
                                        "Price Drop Alert!", JOptionPane.INFORMATION_MESSAGE);
                            }));
                }
            }
        }, 60_000, 60_000);
    }

    // =========================================================================
    // FEATURE 4 — Wishlist
    // =========================================================================
    private void buildWishlistPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(10, 12, 20));
        panel.setName("wishlistPanel");

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("My Wishlist");
        title.setFont(new Font("Arial", Font.BOLD, 20)); title.setForeground(new Color(196, 210, 255));
        JButton shareBtn = new JButton("Share Wishlist");
        styleActionButton(shareBtn, new Color(219, 39, 119));
        shareBtn.addActionListener(e -> shareWishlist());
        header.add(title, BorderLayout.WEST); header.add(shareBtn, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(10, 12, 20));
        listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        listPanel.setName("wishList");

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(null); sp.getViewport().setBackground(new Color(10, 12, 20));
        panel.add(sp, BorderLayout.CENTER);
        contentArea.add(panel, "WISHLIST");
    }

    private void refreshWishlistPanel() {
        for (Component c : contentArea.getComponents()) {
            if (!(c instanceof JPanel) || !"wishlistPanel".equals(c.getName())) continue;
            JScrollPane sp = (JScrollPane) ((JPanel) c).getComponent(1);
            JPanel list = (JPanel) sp.getViewport().getView();
            list.removeAll();

            if (wishlist.isEmpty()) {
                JLabel lbl = new JLabel("Your wishlist is empty. Click Wish on any product!");
                lbl.setForeground(new Color(100, 110, 150)); lbl.setFont(new Font("Arial", Font.ITALIC, 13));
                lbl.setAlignmentX(CENTER_ALIGNMENT);
                list.add(Box.createRigidArea(new Dimension(0, 20))); list.add(lbl);
            } else {
                var products = productService.getAllProducts();
                for (Map.Entry<Integer, String> entry : wishlist.entrySet()) {
                    products.stream().filter(p -> p.getId() == entry.getKey()).findFirst().ifPresent(p -> {
                        JPanel row = new JPanel(new BorderLayout(12, 0));
                        row.setBackground(new Color(18, 22, 38));
                        row.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                                new EmptyBorder(12, 16, 12, 16)));
                        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                        JLabel nameL = new JLabel(p.getName());
                        nameL.setFont(new Font("Arial", Font.BOLD, 13)); nameL.setForeground(new Color(196, 210, 255));

                        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                        right.setOpaque(false);
                        JLabel priceL = new JLabel("Rs. " + String.format("%,.0f", p.getPrice()));
                        priceL.setForeground(new Color(52, 211, 153)); priceL.setFont(new Font("Arial", Font.BOLD, 14));

                        JButton addBtn = miniBtn("Add to Cart", new Color(37, 99, 235));
                        addBtn.addActionListener(ev -> {
                            cartService.addItemToCart(currentUser.getId(), p.getId(), 1);
                            showToast("Added to cart!", true);
                        });
                        JButton delBtn = miniBtn("Remove", new Color(185, 28, 28));
                        delBtn.addActionListener(ev -> { wishlist.remove(entry.getKey()); refreshWishlistPanel(); });

                        right.add(priceL); right.add(addBtn); right.add(delBtn);
                        row.add(nameL, BorderLayout.WEST); row.add(right, BorderLayout.EAST);
                        list.add(row); list.add(Box.createRigidArea(new Dimension(0, 8)));
                    });
                }
            }
            list.revalidate(); list.repaint(); break;
        }
    }

    private void shareWishlist() {
        if (wishlist.isEmpty()) { JOptionPane.showMessageDialog(this, "Your wishlist is empty!"); return; }
        StringBuilder sb = new StringBuilder();
        sb.append(currentUser.getName()).append("'s ShopEasy Wishlist\n");
        sb.append("─────────────────────────────\n");
        var products = productService.getAllProducts();
        for (Map.Entry<Integer, String> entry : wishlist.entrySet())
            products.stream().filter(p -> p.getId() == entry.getKey()).findFirst().ifPresent(p ->
                sb.append("- ").append(p.getName()).append("  Rs.").append(String.format("%,.0f", p.getPrice())).append("\n"));
        sb.append("─────────────────────────────\n");
        sb.append("Code: WL-").append(currentUser.getId()).append("-").append(Math.abs(wishlist.hashCode()) % 9999);

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setBackground(new Color(20, 24, 40)); ta.setForeground(new Color(196, 210, 255));
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13)); ta.setBorder(new EmptyBorder(12, 12, 12, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Share Wishlist", JOptionPane.PLAIN_MESSAGE);
    }

    // =========================================================================
    // Cart
    // =========================================================================
    private void buildCartPanel() {
        cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(new Color(10, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Shopping Cart");
        title.setFont(new Font("Arial", Font.BOLD, 20)); title.setForeground(new Color(196, 210, 255));
        header.add(title, BorderLayout.WEST);
        cartPanel.add(header, BorderLayout.NORTH);

        JPanel cartItems = new JPanel();
        cartItems.setLayout(new BoxLayout(cartItems, BoxLayout.Y_AXIS));
        cartItems.setBackground(new Color(10, 12, 20));
        cartItems.setBorder(new EmptyBorder(10, 20, 10, 20));

        cartScrollPane = new JScrollPane(cartItems);
        cartScrollPane.setBorder(null);
        cartScrollPane.getViewport().setBackground(new Color(10, 12, 20));
        cartScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JButton checkoutBtn = new JButton("Proceed to Checkout");
        styleActionButton(checkoutBtn, new Color(37, 99, 235));
        checkoutBtn.addActionListener(e -> checkoutCart());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottom.setBackground(new Color(15, 18, 30));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 50, 80)));
        bottom.add(checkoutBtn);

        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(bottom, BorderLayout.SOUTH);
        contentArea.add(cartPanel, "CART");
    }

    private void loadCart() {
        JPanel container = (JPanel) cartScrollPane.getViewport().getView();
        container.removeAll();
        var items = cartService.getCartItems(currentUser.getId());
        if (items.isEmpty()) {
            JLabel lbl = new JLabel("Your cart is empty");
            lbl.setFont(new Font("Arial", Font.ITALIC, 14)); lbl.setForeground(new Color(100, 110, 150));
            lbl.setAlignmentX(CENTER_ALIGNMENT);
            container.add(Box.createRigidArea(new Dimension(0, 20))); container.add(lbl);
        } else {
            double total = 0;
            for (var item : items) {
                container.add(createCartItemRow(item));
                container.add(Box.createRigidArea(new Dimension(0, 8)));
                total += item.getProductPrice() * item.getQuantity();
            }
            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(40, 50, 80));
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            container.add(sep); container.add(Box.createRigidArea(new Dimension(0, 8)));
            JLabel tot = new JLabel("Total:  Rs. " + String.format("%,.2f", total));
            tot.setFont(new Font("Arial", Font.BOLD, 18)); tot.setForeground(new Color(52, 211, 153));
            tot.setAlignmentX(RIGHT_ALIGNMENT); container.add(tot);
        }
        container.revalidate(); container.repaint();
    }

    private JPanel createCartItemRow(com.ecommerce.models.CartItem item) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(18, 22, 38));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 80), 1),
                new EmptyBorder(12, 16, 12, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel nameL = new JLabel(item.getProductName());
        nameL.setFont(new Font("Arial", Font.BOLD, 14)); nameL.setForeground(new Color(196, 210, 255));
        nameL.setPreferredSize(new Dimension(240, 30));

        JLabel priceL = new JLabel("Rs. " + String.format("%,.0f", item.getProductPrice()));
        priceL.setForeground(new Color(100, 120, 160)); priceL.setPreferredSize(new Dimension(110, 30));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false); left.add(nameL); left.add(priceL);
        row.add(left, BorderLayout.WEST);

        JSpinner qty = new JSpinner(new SpinnerNumberModel(item.getQuantity(), 1, 99, 1));
        qty.setPreferredSize(new Dimension(65, 30));
        qty.addChangeListener(e -> {
            cartService.updateItemQuantity(item.getCartId(), (Integer) qty.getValue()); loadCart();
        });
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        center.setOpaque(false);
        JLabel qtyL = new JLabel("Qty:"); qtyL.setForeground(new Color(100, 110, 150));
        center.add(qtyL); center.add(qty);
        row.add(center, BorderLayout.CENTER);

        JLabel tot = new JLabel("Rs. " + String.format("%,.0f", item.getProductPrice() * item.getQuantity()));
        tot.setFont(new Font("Arial", Font.BOLD, 14)); tot.setForeground(new Color(52, 211, 153));
        tot.setPreferredSize(new Dimension(110, 30));

        JButton del = miniBtn("Remove", new Color(185, 28, 28));
        del.addActionListener(e -> { cartService.removeItemFromCart(item.getCartId()); loadCart(); });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false); right.add(tot); right.add(del);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void checkoutCart() {
    double totalAmount = cartService.getCartItems(currentUser.getId())
            .stream()
            .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
            .sum();

    String[] options = {"Cash on Delivery", "Online Payment (QR)"};

    int choice = JOptionPane.showOptionDialog(this,
            "Select Payment Method\nTotal: Rs. " + String.format("%,.0f", totalAmount),
            "Payment",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);

    if (choice == 0) {
        placeOrderSuccess(totalAmount, "Cash on Delivery");
    } 
    else if (choice == 1) {
        showQRPayment(totalAmount);
    }
}
    private void showQRPayment(double amount) {
    try {
        String upiId = "manish@upi"; // 🔥 replace with your real UPI
        String name = "ShopEasy";

        String upiUrl = "upi://pay?pa=" + upiId +
                "&pn=" + name +
                "&am=" + amount +
                "&cu=INR";

        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data="
                + java.net.URLEncoder.encode(upiUrl, "UTF-8");

        BufferedImage qrImage = ImageIO.read(new URL(qrUrl));

        // ✅ CREATE CUSTOM DIALOG (THIS FIXES ALIGNMENT)
        JDialog dialog = new JDialog(this, "UPI Payment", true);
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // TOP TEXT
        JLabel title = new JLabel("Scan & Pay Rs. " + String.format("%,.0f", amount), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        // QR CENTER
        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // BOTTOM TEXT
        JLabel note = new JLabel("Click YES after payment", SwingConstants.CENTER);

        // BUTTONS
        JPanel btnPanel = new JPanel();
        JButton yesBtn = new JButton("YES");
        JButton noBtn = new JButton("NO");

        yesBtn.addActionListener(e -> {
            dialog.dispose();
            placeOrderSuccess(amount, "Online Payment");
        });

        noBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);

        // ADD ALL
        panel.add(title, BorderLayout.NORTH);
        panel.add(qrLabel, BorderLayout.CENTER);
        panel.add(note, BorderLayout.SOUTH);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "QR generation failed!");
    }
}

    // =========================================================================
    // Orders — all orders visible, expandable detail with timeline
    // =========================================================================
    private void buildOrdersPanel() {
        ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(new Color(10, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 37, 64)),
                new EmptyBorder(18, 28, 18, 28)));

        JLabel title = new JLabel("My Orders");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(196, 210, 255));

        JLabel hint = new JLabel("Click any order to see delivery details");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setForeground(new Color(74, 82, 112));

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        ordersPanel.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(10, 12, 20));
        list.setBorder(new EmptyBorder(20, 24, 20, 24));

        ordersScrollPane = new JScrollPane(list);
        ordersScrollPane.setBorder(null);
        ordersScrollPane.getViewport().setBackground(new Color(10, 12, 20));
        ordersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
        contentArea.add(ordersPanel, "ORDERS");
    }

    private void loadOrders() {
    ordersPanel.removeAll();
    ordersPanel.setLayout(new BorderLayout());

    List<Order> orders = orderService.getUserOrders(currentUser.getId());

    JPanel listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    listPanel.setBackground(new Color(15, 20, 35));

    if (orders.isEmpty()) {
        JLabel empty = new JLabel("No Orders Yet 😢");
        empty.setForeground(Color.WHITE);
        empty.setFont(new Font("Arial", Font.BOLD, 18));
        empty.setHorizontalAlignment(SwingConstants.CENTER);

        ordersPanel.add(empty, BorderLayout.CENTER);
    } else {
        for (Order order : orders) {

            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            card.setBackground(new Color(25, 30, 50));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            // LEFT INFO
            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.setBackground(new Color(25, 30, 50));

            JLabel orderId = new JLabel("Order #" + order.getId());
            orderId.setForeground(Color.WHITE);
            orderId.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel date = new JLabel(formatOrderDate(order.getOrderDate()));
            date.setForeground(Color.GRAY);

            JLabel status = new JLabel(order.getStatus().toUpperCase());
            status.setForeground(new Color(255, 170, 0));

            left.add(orderId);
            left.add(date);
            left.add(status);

            // RIGHT PRICE
            JLabel price = new JLabel("Rs. " + String.format("%,.0f", order.getTotalAmount()));
            price.setForeground(new Color(0, 255, 180));
            price.setFont(new Font("Arial", Font.BOLD, 16));

            // ADD TO CARD
            card.add(left, BorderLayout.WEST);
            card.add(price, BorderLayout.EAST);

            card = createExpandableOrderCard(order, listPanel);

            // wrapper to center card
            JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerWrapper.setBackground(new Color(15, 20, 35));
            centerWrapper.add(card);

            listPanel.add(centerWrapper);
            listPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        ordersPanel.add(scroll, BorderLayout.CENTER);
    }

    ordersPanel.revalidate();
    ordersPanel.repaint();
}

    private JPanel createExpandableOrderCard(com.ecommerce.models.Order order, JPanel parentContainer) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(18, 24, 40));
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(30, 37, 64), 1));
        wrapper.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        wrapper.setPreferredSize(new Dimension(800, wrapper.getPreferredSize().height));

        // Summary row
        JPanel summary = new JPanel(new BorderLayout(16, 0));
        summary.setBackground(new Color(18, 24, 40));
        summary.setBorder(new EmptyBorder(16, 20, 16, 20));
        summary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        JPanel idRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idRow.setOpaque(false);
        JLabel orderIdLbl = new JLabel("Order #" + order.getId());
        orderIdLbl.setFont(new Font("Arial", Font.BOLD, 15));
        orderIdLbl.setForeground(new Color(196, 210, 255));
        JLabel statusBadge = buildStatusBadge(order.getStatus());
        idRow.add(orderIdLbl);
        idRow.add(Box.createRigidArea(new Dimension(10, 0)));
        idRow.add(statusBadge);

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateRow.setOpaque(false);
        JLabel dateLbl = new JLabel("  " + formatOrderDate(order.getOrderDate()));
        dateLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLbl.setForeground(new Color(74, 82, 112));
        dateRow.add(dateLbl);

        leftCol.add(idRow);
        leftCol.add(Box.createRigidArea(new Dimension(0, 5)));
        leftCol.add(dateRow);
        summary.add(leftCol, BorderLayout.CENTER);

        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightCol.setOpaque(false);
        JLabel totalLbl = new JLabel("Rs. " + String.format("%,.0f", order.getTotalAmount()));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 17));
        totalLbl.setForeground(new Color(52, 211, 153));
        JLabel toggleLbl = new JLabel("Details  v");
        toggleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        toggleLbl.setForeground(new Color(74, 98, 148));
        rightCol.add(totalLbl); rightCol.add(toggleLbl);
        summary.add(rightCol, BorderLayout.EAST);
        wrapper.add(summary);

        // Detail panel — hidden by default
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(new Color(13, 17, 32));
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(30, 37, 64)),
                new EmptyBorder(18, 24, 20, 24)));
        detailPanel.setVisible(false);

        // Delivery timeline
        detailPanel.add(sectionLabel("DELIVERY TRACKER"));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailPanel.add(buildTimeline(order));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // Estimated delivery info
        JPanel estimateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        estimateRow.setOpaque(false);
        estimateRow.setAlignmentX(LEFT_ALIGNMENT);
        String estDelivery = estimateDelivery(order);
        JLabel estLbl = new JLabel("Estimated Delivery: " + estDelivery);
        estLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        estLbl.setForeground(new Color(251, 191, 36));
        estimateRow.add(estLbl);
        detailPanel.add(estimateRow);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 37, 64));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        detailPanel.add(sep);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Order summary info
        detailPanel.add(sectionLabel("ORDER SUMMARY"));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        infoRow.setOpaque(false); infoRow.setAlignmentX(LEFT_ALIGNMENT);
        infoRow.add(infoItem("Order ID",  "#" + order.getId()));
        infoRow.add(infoItem("Total",     "Rs. " + String.format("%,.2f", order.getTotalAmount())));
        infoRow.add(infoItem("Status",    order.getStatus().toUpperCase()));
        infoRow.add(infoItem("Placed on", formatOrderDate(order.getOrderDate())));
        detailPanel.add(infoRow);

        wrapper.add(detailPanel);

        // Toggle on click
        summary.addMouseListener(new java.awt.event.MouseAdapter() {
            boolean expanded = false;
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                expanded = !expanded;
                detailPanel.setVisible(expanded);
                toggleLbl.setText(expanded ? "Hide  ^" : "Details  v");
                wrapper.revalidate(); wrapper.repaint();
                parentContainer.revalidate(); parentContainer.repaint();
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { summary.setBackground(new Color(22, 30, 52)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { summary.setBackground(new Color(18, 24, 40)); }
        });

        return wrapper;
    }

    /** Estimates delivery date based on order date + status */
    private String estimateDelivery(com.ecommerce.models.Order order) {
        if (order.getOrderDate() == null) return "—";
        boolean done = "completed".equalsIgnoreCase(order.getStatus())
                || "delivered".equalsIgnoreCase(order.getStatus());
        if (done) return "Delivered";
        LocalDateTime est = order.getOrderDate().plusDays(5);
        return est.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private JPanel buildTimeline(com.ecommerce.models.Order order) {
        String[] stages   = {"Order Placed", "Processing", "Shipped", "Out for Delivery", "Delivered"};
        String   status   = order.getStatus() == null ? "" : order.getStatus().toLowerCase();

        // Map status to active stage index
        int activeAt;
        switch (status) {
            case "completed":
            case "delivered":  activeAt = 5; break;
            case "shipped":    activeAt = 3; break;
            case "processing": activeAt = 2; break;
            default:           activeAt = 1; break; // pending
        }

        JPanel tl = new JPanel(new GridLayout(1, stages.length, 0, 0));
        tl.setOpaque(false);
        tl.setAlignmentX(LEFT_ALIGNMENT);
        tl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        for (int i = 0; i < stages.length; i++) {
            final int idx = i;
            JPanel step = new JPanel();
            step.setLayout(new BoxLayout(step, BoxLayout.Y_AXIS));
            step.setOpaque(false);

            JPanel dotRow = new JPanel(new BorderLayout());
            dotRow.setOpaque(false);

            JPanel lineLeft = new JPanel();
            lineLeft.setPreferredSize(new Dimension(0, 2));
            lineLeft.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            lineLeft.setBackground(idx > 0 && idx < activeAt ? new Color(52, 211, 153) : new Color(30, 37, 64));
            lineLeft.setOpaque(idx > 0);

            JPanel lineRight = new JPanel();
            lineRight.setPreferredSize(new Dimension(0, 2));
            lineRight.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            lineRight.setBackground(idx < activeAt - 1 ? new Color(52, 211, 153) : new Color(30, 37, 64));
            lineRight.setOpaque(idx < stages.length - 1);

            Color dotBg, dotBorder;
            if (idx < activeAt) {
                dotBg = new Color(15, 61, 34); dotBorder = new Color(52, 211, 153);
            } else if (idx == activeAt) {
                dotBg = new Color(14, 28, 56); dotBorder = new Color(96, 165, 250);
            } else {
                dotBg = new Color(13, 17, 32); dotBorder = new Color(30, 37, 64);
            }

            JPanel dot = new JPanel() {
                @Override protected void paintComponent(java.awt.Graphics g) {
                    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    int s = Math.min(getWidth(), getHeight());
                    int x = (getWidth() - s) / 2, y = (getHeight() - s) / 2;
                    g2.setColor(dotBg); g2.fillOval(x, y, s - 1, s - 1);
                    g2.setColor(dotBorder);
                    g2.setStroke(new java.awt.BasicStroke(idx < activeAt ? 2f : 1.5f));
                    g2.drawOval(x, y, s - 1, s - 1);
                    if (idx < activeAt) {
                        g2.setColor(new Color(52, 211, 153));
                        int inner = s / 3;
                        g2.fillOval(x + (s - inner) / 2, y + (s - inner) / 2, inner, inner);
                    } else if (idx == activeAt) {
                        g2.setColor(new Color(96, 165, 250));
                        int inner = s / 3;
                        g2.fillOval(x + (s - inner) / 2, y + (s - inner) / 2, inner, inner);
                    }
                    g2.dispose();
                }
            };
            dot.setPreferredSize(new Dimension(18, 18));
            dot.setMinimumSize(new Dimension(18, 18));
            dot.setMaximumSize(new Dimension(18, 18));
            dot.setOpaque(false);

            JPanel dotCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 9));
            dotCenter.setOpaque(false); dotCenter.add(dot);

            if (idx == 0) dotRow.add(new JPanel() {{ setOpaque(false); }}, BorderLayout.WEST);
            else dotRow.add(lineLeft, BorderLayout.WEST);
            dotRow.add(dotCenter, BorderLayout.CENTER);
            if (idx == stages.length - 1) dotRow.add(new JPanel() {{ setOpaque(false); }}, BorderLayout.EAST);
            else dotRow.add(lineRight, BorderLayout.EAST);

            Color labelColor = idx < activeAt ? new Color(52, 211, 153)
                    : idx == activeAt ? new Color(96, 165, 250)
                    : new Color(58, 68, 96);
            JLabel lbl = new JLabel("<html><center>" + stages[i] + "</center></html>", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.PLAIN, 10));
            lbl.setForeground(labelColor);

            step.add(dotRow); step.add(lbl);
            tl.add(step);
        }
        return tl;
    }

    private JLabel buildStatusBadge(String status) {
        boolean completed  = "completed".equalsIgnoreCase(status) || "delivered".equalsIgnoreCase(status);
        boolean processing = "processing".equalsIgnoreCase(status) || "shipped".equalsIgnoreCase(status);
        Color bg     = completed ? new Color(10, 40, 24) : processing ? new Color(14, 28, 56) : new Color(42, 30, 8);
        Color fg     = completed ? new Color(52, 211, 153) : processing ? new Color(96, 165, 250) : new Color(247, 183, 49);
        Color border = completed ? new Color(15, 61, 34) : processing ? new Color(21, 42, 85) : new Color(61, 46, 10);

        return new JLabel(status.toUpperCase()) {
            { setFont(new Font("Arial", Font.BOLD, 11)); setForeground(fg); setOpaque(false); setBorder(new EmptyBorder(3, 10, 3, 10)); }
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(border); g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose(); super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize(); d.height = Math.max(d.height, 22); return d;
            }
        };
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setForeground(new Color(74, 82, 112));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel infoItem(String label, String value) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11)); lbl.setForeground(new Color(74, 82, 112));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 13)); val.setForeground(new Color(196, 210, 255));
        item.add(lbl); item.add(Box.createRigidArea(new Dimension(0, 3))); item.add(val);
        return item;
    }

    private String formatOrderDate(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"));
    }

    // =========================================================================
    // Admin
    // =========================================================================
    private void buildAdminPanel() {
        adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(new Color(10, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 18, 30));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Admin Panel");
        title.setFont(new Font("Arial", Font.BOLD, 20)); title.setForeground(new Color(196, 210, 255));
        header.add(title, BorderLayout.WEST);
        adminPanel.add(header, BorderLayout.NORTH);

        JLabel ph = new JLabel("Admin functions will be implemented here", SwingConstants.CENTER);
        ph.setForeground(new Color(100, 110, 150)); ph.setFont(new Font("Arial", Font.ITALIC, 13));
        adminPanel.add(ph, BorderLayout.CENTER);
        contentArea.add(adminPanel, "ADMIN");
    }

    // =========================================================================
    // Navigation
    // =========================================================================
    private void showLoginScreen()    { cardLayout.show(mainPanel, "LOGIN");    setTitle("ShopEasy - Login"); }
    private void showRegisterScreen() { cardLayout.show(mainPanel, "REGISTER"); setTitle("ShopEasy - Register"); }

    private void showMainApp() {
        navAdminBtn.setVisible("admin".equalsIgnoreCase(currentUser.getRole()));
        cardLayout.show(mainPanel, "MAIN_APP");
        setTitle("ShopEasy - " + currentUser.getName());
        showHome();
    }

    private void showHome() {
        // First login vs returning user greeting
        String prefKey = "firstLogin_" + currentUser.getEmail();
        boolean isFirstTime = prefs.getBoolean(prefKey, false);

        if (isFirstTime) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
            // Mark as no longer first time
            prefs.putBoolean(prefKey, false);
        } else {
            welcomeLabel.setText("Welcome Back, " + currentUser.getName() + "!");
        }

        contentCardLayout.show(contentArea, "HOME");
    }

    private void showProductCatalog() { loadProducts();          contentCardLayout.show(contentArea, "PRODUCTS"); }
    private void showCart()           { loadCart();              contentCardLayout.show(contentArea, "CART"); }
    private void showOrders()         { loadOrders();            contentCardLayout.show(contentArea, "ORDERS"); }
    private void showWishlist()       { refreshWishlistPanel();  contentCardLayout.show(contentArea, "WISHLIST"); }
    private void showMoodShop()       { contentCardLayout.show(contentArea, "MOOD"); }
    private void showPriceAlerts()    { refreshAlertPanel();     contentCardLayout.show(contentArea, "ALERTS"); }
    private void showAdminPanel()     {
        if ("admin".equalsIgnoreCase(currentUser.getRole()))
            contentCardLayout.show(contentArea, "ADMIN");
    }

    private void logout() {
        currentUser = null; priceAlerts.clear(); wishlist.clear(); showLoginScreen();
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private void showToast(String msg, boolean success) {
        JWindow toast = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setOpaque(true);
        lbl.setBackground(success ? new Color(6, 78, 59) : new Color(127, 29, 29));
        lbl.setForeground(success ? new Color(52, 211, 153) : new Color(252, 165, 165));
        lbl.setBorder(new EmptyBorder(10, 16, 10, 16));
        toast.add(lbl); toast.pack();
        toast.setLocation(getX() + getWidth() / 2 - toast.getWidth() / 2, getY() + getHeight() - 80);
        toast.setVisible(true);
        new Timer(true).schedule(new TimerTask() {
            @Override public void run() { SwingUtilities.invokeLater(() -> toast.setVisible(false)); }
        }, 2500);
    }

    private JButton miniBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.PLAIN, 11));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(5, 8, 5, 8)); b.setOpaque(true);
        return b;
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24)); btn.setOpaque(true);
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(25, 30, 50)); field.setForeground(new Color(196, 210, 255));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
                new EmptyBorder(8, 10, 8, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private void addFormFieldLabel(JPanel panel, GridBagConstraints gbc, String text, int row) {
        JLabel lbl = new JLabel(text + ":");
        lbl.setForeground(new Color(150, 160, 200)); lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; panel.add(lbl, gbc);
    }

    private String getImageUrl(String name) {
        String lower = name.toLowerCase();
        for (Map.Entry<String, String> e : IMAGE_URLS.entrySet())
            if (lower.contains(e.getKey())) return e.getValue();
        return "https://images.unsplash.com/photo-1518770660439-4636190af475?w=220&h=140&fit=crop";
    }

    private String getApiKey() {
        return "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";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // =========================================================================
    // WrapLayout
    // =========================================================================
    public static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container t) { return layoutSize(t, true); }
        @Override public Dimension minimumLayoutSize(Container t)   { return layoutSize(t, false); }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int tw = target.getSize().width; if (tw == 0) tw = Integer.MAX_VALUE;
                Insets ins = target.getInsets(); int maxW = tw - ins.left - ins.right;
                Dimension dim = new Dimension(0, 0); int rw = 0, rh = 0;
                for (Component c : target.getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                    if (rw + d.width > maxW && rw > 0) { dim.width = Math.max(dim.width, rw); dim.height += rh + getVgap(); rw = 0; rh = 0; }
                    rw += d.width + getHgap(); rh = Math.max(rh, d.height);
                }
                dim.width = Math.max(dim.width, rw);
                dim.height += rh + ins.top + ins.bottom + getVgap() * 2;
                return dim;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ECommerceGUI().setVisible(true));
    }
}