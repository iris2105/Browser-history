import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrowserHistoryAppSwingTemplate extends JFrame {

    // Inner Page class to represent each page in the browser history
    class Page {
        String url;
        Page prev, next;

        public Page(String url) {
            this.url = url;
            prev = next = null;
        }
    }

    // Inner History class to manage history, bookmarks, undo, and redo
    class History {
        private Page current;
        private List<String> bookmarks = new ArrayList<>();
        private List<Page> undoStack = new ArrayList<>();
        private List<Page> redoStack = new ArrayList<>();

        // Visit a new page
        public void visitPage(String url) {
            Page newPage = new Page(url);
            if (current != null) {
                current.next = newPage;
                newPage.prev = current;
            }
            undoStack.add(current);
            current = newPage;
            redoStack.clear(); // Clear redo stack on a new visit
        }

        // Go back in history
        public void goBack() {
            if (current != null && current.prev != null) {
                redoStack.add(current);
                current = current.prev;
            }
        }

        // Go forward in history
        public void goForward() {
            if (current != null && current.next != null) {
                undoStack.add(current);
                current = current.next;
            }
        }

        // Undo functionality
        public void undo() {
            if (!undoStack.isEmpty()) {
                redoStack.add(current);
                current = undoStack.remove(undoStack.size() - 1);
            }
        }

        // Redo functionality
        public void redo() {
            if (!redoStack.isEmpty()) {
                undoStack.add(current);
                current = redoStack.remove(redoStack.size() - 1);
            }
        }

        // Bookmark current page
        public void bookmarkPage() {
            if (current != null) {
                bookmarks.add(current.url);
            }
        }

        // Clear history
        public void clearHistory() {
            current = null;
            undoStack.clear();
            redoStack.clear();
        }

        // Get history as a list of URLs
        public List<String> getHistory() {
            List<String> historyList = new ArrayList<>();
            Page temp = current;
            while (temp != null) {
                historyList.add(temp.url);
                temp = temp.prev;
            }
            return historyList;
        }

        // Get bookmarks as a list of URLs
        public List<String> getBookmarks() {
            return bookmarks;
        }

        // Search history for URLs that contain the search term
        public List<String> searchHistory(String searchTerm) {
            List<String> searchResults = new ArrayList<>();
            for (String url : getHistory()) {
                if (url.toLowerCase().contains(searchTerm.toLowerCase())) {
                    searchResults.add(url);
                }
            }
            return searchResults;
        }
    }

    private History browserHistory = new History();
    private DefaultListModel<String> historyModel = new DefaultListModel<>();
    private JTextField urlInput = new JTextField(30);
    private JTextField searchInput = new JTextField(20); // Search input field
    private JList<String> historyView = new JList<>(historyModel);
    private JLabel resultLabel = new JLabel(""); // Label for displaying search results

    public BrowserHistoryAppSwingTemplate() {
        setTitle("Browser History Manager - Swing Template");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main layout setup
        setLayout(new BorderLayout());

        // Setup navigation panel with URL input and buttons
        JPanel navigationPanel = new JPanel(new FlowLayout());
        setupNavigationPanel(navigationPanel);

        // Setup search panel with search bar and buttons
        JPanel searchPanel = new JPanel(new FlowLayout());
        setupSearchPanel(searchPanel);

        // History view setup
        setupHistoryView();

        // Add components to main layout
        add(navigationPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.AFTER_LAST_LINE);
        add(new JScrollPane(historyView), BorderLayout.CENTER);

        // Show the JFrame
        setVisible(true);
    }

    // Sets up the navigation panel with URL input and buttons
    private void setupNavigationPanel(JPanel navigationPanel) {
        urlInput.setToolTipText("Enter URL...");
        JButton visitButton = new JButton("Visit");
        JButton backButton = new JButton("Back");
        JButton forwardButton = new JButton("Forward");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton clearHistoryButton = new JButton("Clear History");
        JButton bookmarkButton = new JButton("Bookmark Page");
        JButton viewBookmarksButton = new JButton("View Bookmarks");

        // Add components to navigation panel
        navigationPanel.add(urlInput);
        navigationPanel.add(visitButton);
        navigationPanel.add(backButton);
        navigationPanel.add(forwardButton);
        navigationPanel.add(undoButton);
        navigationPanel.add(redoButton);
        navigationPanel.add(clearHistoryButton);
        navigationPanel.add(bookmarkButton);
        navigationPanel.add(viewBookmarksButton);

        // Set up action listeners for each button
        visitButton.addActionListener(e -> visitPage());
        backButton.addActionListener(e -> goBack());
        forwardButton.addActionListener(e -> goForward());
        undoButton.addActionListener(e -> undo());
        redoButton.addActionListener(e -> redo());
        clearHistoryButton.addActionListener(e -> clearHistory());
        bookmarkButton.addActionListener(e -> bookmarkPage());
        viewBookmarksButton.addActionListener(e -> openBookmarkWindow());
    }

    // Sets up the search panel with a search field, buttons, and result label
    private void setupSearchPanel(JPanel searchPanel) {
        searchInput.setToolTipText("Search history...");
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset Search");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchInput);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.add(resultLabel); // Add the result label to the search panel

        // Set up action listeners for search and reset buttons
        searchButton.addActionListener(e -> searchHistory());
        resetButton.addActionListener(e -> resetHistoryView());
    }

    // Sets up the history view with a titled border
    private void setupHistoryView() {
        historyView.setBorder(BorderFactory.createTitledBorder("History"));
        historyView.setPreferredSize(new Dimension(500, 300));
    }

    // Visit page action
    private void visitPage() {
        String url = urlInput.getText().trim();
        if (!url.isEmpty()) {
            browserHistory.visitPage(url);
            updateHistoryView();
            urlInput.setText("");
        }
    }

    // Go back in history
    private void goBack() {
        browserHistory.goBack();
        updateHistoryView();
    }

    // Go forward in history
    private void goForward() {
        browserHistory.goForward();
        updateHistoryView();
    }

    // Undo action
    private void undo() {
        browserHistory.undo();
        updateHistoryView();
    }

    // Redo action
    private void redo() {
        browserHistory.redo();
        updateHistoryView();
    }

    // Clear history action
    private void clearHistory() {
        browserHistory.clearHistory();
        updateHistoryView();
    }

    // Bookmark current page
    private void bookmarkPage() {
        browserHistory.bookmarkPage();
    }

    // Open a new window to display bookmarks
    private void openBookmarkWindow() {
        JFrame bookmarkFrame = new JFrame("Bookmarks");
        bookmarkFrame.setSize(400, 300);
        bookmarkFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Bookmarks List
        DefaultListModel<String> bookmarkModel = new DefaultListModel<>();
        for (String url : browserHistory.getBookmarks()) {
            bookmarkModel.addElement(url);
        }
        JList<String> bookmarkList = new JList<>(bookmarkModel);
        bookmarkList.setBorder(BorderFactory.createTitledBorder("Bookmarks"));

        // Add bookmark list to frame and display
        bookmarkFrame.add(new JScrollPane(bookmarkList));
        bookmarkFrame.setVisible(true);
    }

    // Search history based on input and update result label
    private void searchHistory() {
        String searchTerm = searchInput.getText().trim();
        if (!searchTerm.isEmpty()) {
            List<String> searchResults = browserHistory.searchHistory(searchTerm);
            historyModel.clear();
            if (!searchResults.isEmpty()) {
                for (String url : searchResults) {
                    historyModel.addElement(url);
                }
                resultLabel.setText("Match found");
            } else {
                resultLabel.setText("No match found");
            }
        }
    }

    // Reset the history view to display full history and clear result label
    private void resetHistoryView() {
        searchInput.setText(""); // Clear the search input field
        resultLabel.setText(""); // Clear the search result label
        updateHistoryView();
    }

    // Update history view
    private void updateHistoryView() {
        historyModel.clear();
        for (String url : browserHistory.getHistory()) {
            historyModel.addElement(url);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BrowserHistoryAppSwingTemplate::new);
    }
}
