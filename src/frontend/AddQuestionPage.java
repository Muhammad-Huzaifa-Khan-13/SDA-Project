package frontend;

import backend.controllers.QuestionController;
import backend.models.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddQuestionPage extends JDialog {

    private int quizId;
    private JTextArea questionArea;
    private JTextField optA, optB, optC, optD;
    private JComboBox<String> correctCombo;
    private JButton addBtn;

    private QuestionController questionController = new QuestionController();

    // track whether this dialog was opened right after quiz creation
    private boolean autoCreated = false;
    // track whether at least one question has been added in this dialog
    private boolean questionAdded = false;

    public AddQuestionPage(Dialog parent, int quizId) {
        super(parent, "Add Question", true);
        this.quizId = quizId;
        setSize(640, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    // New constructor to accept autoCreated flag
    public AddQuestionPage(Dialog parent, int quizId, boolean autoCreated) {
        super(parent, "Add Question", true);
        this.quizId = quizId;
        this.autoCreated = autoCreated;
        setSize(640, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIUtils.BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.BOTH;

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add Question (Quiz ID: " + quizId + ")");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Question:"), gbc);
        questionArea = new JTextArea(4, 40);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(UIUtils.REGULAR_FONT);
        JScrollPane sp = new JScrollPane(questionArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(sp, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Option A:"), gbc);
        optA = new JTextField(); optA.setPreferredSize(new Dimension(320, 26));
        gbc.gridx = 1; card.add(optA, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Option B:"), gbc);
        optB = new JTextField(); optB.setPreferredSize(new Dimension(320, 26));
        gbc.gridx = 1; card.add(optB, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("Option C:"), gbc);
        optC = new JTextField(); optC.setPreferredSize(new Dimension(320, 26));
        gbc.gridx = 1; card.add(optC, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Option D:"), gbc);
        optD = new JTextField(); optD.setPreferredSize(new Dimension(320, 26));
        gbc.gridx = 1; card.add(optD, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        card.add(new JLabel("Correct Option:"), gbc);
        correctCombo = new JComboBox<>(new String[]{"A","B","C","D"});
        gbc.gridx = 1; card.add(correctCombo, gbc);

        addBtn = new JButton("Add Question");
        UIUtils.applyPrimaryButton(addBtn);
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addQuestion();
            }
        });

        // New Confirm and Return buttons
        JButton confirmBtn = new JButton("Confirm");
        UIUtils.applyPrimaryButton(confirmBtn);
        confirmBtn.addActionListener(e -> addQuestionAndClose());

        JButton returnBtn = new JButton("Return");
        UIUtils.applySecondaryButton(returnBtn);
        returnBtn.addActionListener(e -> handleReturn());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setOpaque(false);
        btns.add(addBtn);
        btns.add(confirmBtn);
        btns.add(returnBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        card.add(btns, gbc);

        c.gridx = 0; c.gridy = 0;
        root.add(card, c);

        add(root);

        // Ensure closing the window behaves like pressing Return
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleReturn();
            }
        });
    }

    private boolean addQuestion() {
        String qtext = questionArea.getText().trim();
        String a = optA.getText().trim();
        String b = optB.getText().trim();
        String c = optC.getText().trim();
        String d = optD.getText().trim();
        String correct = (String) correctCombo.getSelectedItem();

        if (qtext.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Question q = new Question(0, quizId, qtext, a, b, c, d, correct);
        boolean ok = questionController.addQuestion(q);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to add question", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JOptionPane.showMessageDialog(this, "Question added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        // clear fields
        questionArea.setText(""); optA.setText(""); optB.setText(""); optC.setText(""); optD.setText("");
        correctCombo.setSelectedIndex(0);
        questionAdded = true;
        return true;
    }

    private void addQuestionAndClose() {
        boolean ok = addQuestion();
        if (ok) {
            dispose();
        }
    }

    private void handleReturn() {
        boolean hasData = false;
        if (!questionArea.getText().trim().isEmpty()) hasData = true;
        if (!optA.getText().trim().isEmpty()) hasData = true;
        if (!optB.getText().trim().isEmpty()) hasData = true;
        if (!optC.getText().trim().isEmpty()) hasData = true;
        if (!optD.getText().trim().isEmpty()) hasData = true;

        boolean doDispose = false;
        if (hasData) {
            int res = JOptionPane.showConfirmDialog(this, "Discard changes and return?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) doDispose = true;
        } else {
            doDispose = true;
        }

        if (doDispose) {
            // If this dialog was opened immediately after quiz creation and the user added no questions,
            // we should remove the created quiz so CreateQuizPage can return to previous state.
            if (autoCreated && !questionAdded) {
                // parent is expected to be CreateQuizPage (a JDialog). Attempt to notify it.
                Dialog parent = (Dialog) getOwner();
                if (parent instanceof CreateQuizPage) {
                    ((CreateQuizPage) parent).quizCanceled(quizId);
                }
            }
            dispose();
        }
    }
}