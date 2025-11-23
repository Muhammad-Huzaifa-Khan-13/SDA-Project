package frontend;

import backend.controllers.QuestionController;
import backend.models.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddQuestionPage extends JDialog {

    private int quizId;
    private JTextArea questionArea;
    private JTextField optA, optB, optC, optD;
    private JComboBox<String> correctCombo;
    private JButton addBtn;

    private QuestionController questionController = new QuestionController();

    public AddQuestionPage(Dialog parent, int quizId) {
        super(parent, "Add Question", true);
        this.quizId = quizId;
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

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setOpaque(false);
        btns.add(addBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        card.add(btns, gbc);

        c.gridx = 0; c.gridy = 0;
        root.add(card, c);

        add(root);
    }

    private void addQuestion() {
        String qtext = questionArea.getText().trim();
        String a = optA.getText().trim();
        String b = optB.getText().trim();
        String c = optC.getText().trim();
        String d = optD.getText().trim();
        String correct = (String) correctCombo.getSelectedItem();

        if (qtext.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Question q = new Question(0, quizId, qtext, a, b, c, d, correct);
        boolean ok = questionController.addQuestion(q);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to add question", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Question added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        // clear fields
        questionArea.setText(""); optA.setText(""); optB.setText(""); optC.setText(""); optD.setText("");
        correctCombo.setSelectedIndex(0);
    }
}