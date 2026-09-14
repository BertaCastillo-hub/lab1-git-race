---
title: "Web Engineering 2026-2027"
subtitle: "Lab 1: Git Race"
date: "2026-09-14"
format:
  html:
    toc: true
    toc-depth: 3
    number-sections: true
    code-fold: true
    code-tools: true
    theme: cosmo
    css: |
      body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
      .quarto-title-block { border-bottom: 2px solid #2c3e50; padding-bottom: 1rem; }
      h1, h2, h3 { color: #2c3e50; }
      pre { background-color: #f8f9fa; border-left: 4px solid #007acc; }
      .callout { border-left: 4px solid #28a745; }
  pdf:
    documentclass: article
    classoption: [11pt, a4paper]
    toc: true
    toc-depth: 3
    number-sections: true
    geometry: [margin=2.5cm, headheight=15pt]
    fontsize: 11pt
    linestretch: 1.15
    colorlinks: true
    breakurl: true
    urlcolor: blue
    linkcolor: blue
    citecolor: blue
    hyperrefoptions:
      - linktoc=all
      - bookmarksnumbered=true
      - bookmarksopen=true
    header-includes:
      - |
        \usepackage{helvet}
        \renewcommand{\familydefault}{\sfdefault}
        \usepackage{hyperref}
        \usepackage{fancyhdr}
        \pagestyle{fancy}
        \fancyhf{}
        \fancyhead[L]{Web Engineering 2026-2027}
        \fancyhead[R]{Lab 1: Git Race}
        \fancyfoot[C]{\thepage}
        \renewcommand{\headrulewidth}{0.4pt}
        \usepackage{microtype}
        \usepackage{booktabs}
        \usepackage{array}
        \usepackage{longtable}
        \usepackage{xcolor}
        \definecolor{sectioncolor}{RGB}{44,62,80}
        \usepackage{sectsty}
        \allsectionsfont{\color{sectioncolor}}
        \usepackage{fvextra}
        \DefineVerbatimEnvironment{Highlighting}{Verbatim}{commandchars=\\\{\},breaklines,breaknonspaceingroup,breakanywhere}
lang: en
---

First lab of 2026--2027. Command line is enough; **VS Code**, **IntelliJ IDEA**, **Eclipse**, or **GitHub Codespaces** are fine. You need **Java 25 LTS** (same pin as the group project).

## Requirements

- [Kotlin 2.4.0](https://kotlinlang.org/) on **Java 25 LTS**. Do not downgrade the toolchain.
- [Gradle 9.6.0](https://gradle.org/) via the wrapper (`./gradlew`).
- [Spring Boot 4.1.0](https://docs.spring.io/spring-boot/) (Spring Framework 7). Use `spring-boot-starter-webmvc`, not the Boot 3 name `spring-boot-starter-web`.
- **Bootstrap 5.3.8** and **Thymeleaf**.

## Git and clone

1. Install [git](https://git-scm.com/).
2. GitHub education account: real name, university email.
3. Configure Git:

   ```bash
   git config --global user.name "Your Real Name"
   git config --global user.email "your_nip@unizar.es"
   ```

4. Authenticate with GitHub over HTTPS or SSH ([setup guide](https://docs.github.com/en/get-started/quickstart/set-up-git)).

```bash
git clone \
  https://github.com/UNIZAR-30246-WebEngineering/lab1-git-race.git
cd lab1-git-race
./gradlew check
./gradlew bootRun
```

Open <http://localhost:8080>. Also: `/api/hello`, `/actuator/health`.

### Codespaces (optional)

You can do the lab in [GitHub Codespaces](https://docs.github.com/en/codespaces) instead of a local JDK. Forward port **8080** when you `./gradlew bootRun`.

If you use Codespaces, you may **fork** the starter and create the Codespace from that fork. That is the usual path: you cannot push to the course repository, so the GitHub submission is your fork.

## Objective

Add **at least 100 lines** of useful code and documentation. Document in the source and in **REPORT.md**. Kotlin: [KDoc](https://kotlinlang.org/docs/kotlin-doc.html). Markdown: [GitHub syntax](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax).

The starter already has a page, a JSON endpoint, tests, and Actuator. Extend it.

The focus is **backend** (Kotlin / Spring: controller, data, tests) **and how the UI talks to it** — not UI-only work. A restyle, extra markup, or client-only script with no server behaviour does not meet the objective.

Examples that fit this lab:

- Greeting that depends on time of day or `Accept-Language` / a `lang` query (server decides; page and JSON both show it)
- In-memory greeting log (name, timestamp) exposed as JSON and/or rendered on the page
- A preference the **server** stores (cookie or query) and the template uses — not CSS-only theming
- Tests for the behaviour you add
- KDoc on the controller and a short note in `README.md` for *your* increment — not a rewrite of this guide

Stay on the hello-app. Auth, WebSockets, APM, and a CI product are out of scope unless the teacher **accepted** a bonus proposal that includes them.

### What to document

English, at least B1. Cover what **you** added: request/response, properties, how to run and test it. Commands in the report must actually work.

### Oral defence (all students)

An oral defence is **possible for everyone**, not only for the bonus. The teacher may ask you to explain this lab in person.

You are expected to **know** the submission and to **justify all decisions** in it: what you specified, what you changed, what you rejected, how you verified it, and any AI use. If you cannot explain a choice, you do not own it.

### Bonus

The bonus is optional. The bonus is **+0.05** on factor **k** (labs; cap **+0.10** across the six labs).

It is not extra work you add at submit time. Sequence:

1. The **student proposes** the extra (what you will do, why it is outstanding, how git history will stay readable). Propose during the lab window, **before** you treat that extra as bonus work.
2. The **instructor accepts** — or refuses if the proposal is too large, off-scope, or late.
3. **Only then** may that extra be done as bonus work.

After acceptance implement what was agreed, with tests and documentation another student could follow, then defend it in person. 

Git is **mandatory** for the bonus: imperative commit subjects, small logical commits, feature branches, no dump-everything commits. `git rebase -i` is allowed to tidy *your* branch before you push; do not rewrite history you already shared.

```bash
git checkout -b feature/your-feature-name
git add …
git commit -m "Add time-based greeting"
git push origin feature/your-feature-name
```

## Finish and submit

Keep the **REPORT.md** headings. It is an engineering note (specified, changed, verified, AI), not a restatement of the starter README.

**Only files under git control are reviewed.** Commit what you want evaluated. Untracked files do not count.

The Moodle zip must include the **`.git` directory** (the whole repository, not only the working tree).

```bash
./gradlew check
./gradlew clean
zip -r lab1-git-race-submission.zip . \
  -x ".gradle/*" ".kotlin/*" ".idea/*" "*.iml" ".DS_Store"
```

Do not zip Gradle/Kotlin caches or IDE files.

Deadline **25 September 2026, 23:59** (one week after the last Lab 1 session, 11:59 pm). Two channels:

1. Moodle zip (after `./gradlew clean`, **with `.git`**).
2. Push to GitHub for evaluation (your **fork**, if you made one — Codespaces path).

### REPORT.md

Do **not** delete the headings. Do **not** report a percentage of “AI vs original lines”.

1. **What I specified** — increment *before* generating or pasting code, and how you would know it works
2. **What I changed** — files and behaviour
3. **Technical decisions** — choices you own (and what you rejected)
4. **How I verified** — `./gradlew check`, what failed first, what you fixed
5. **AI disclosure** — the [AI use](#ai-use) table, **or** **No AI assistance**

**Thin (fails):** `Tools: ChatGPT. Used for: the assignment.`

**Adequate:** named tool; one concrete purpose; a prompt or two; files touched; `./gradlew check` plus what you changed after the model’s draft.

## AI use

The group project scores **AI use (10%)**: GenAI and agents to boost delivery, with disclosure you can defend. This lab is where you **practise that disclosure**. Lab 1 itself stays **limited**: assistive GenAI only — **not** a full or substantial generated solution. You do **not** need `AGENTS.md` or a skill here.

If you cannot explain it without the tool, you do not own it. This course grades **web engineering judgment** (specify, design, verify).

### Allowed (assistive)

- Brainstorming and outlines
- Boilerplate and scaffolding
- Debugging help; refactoring and lint fixes
- Test skeletons
- Grammar and style on **REPORT.md**

### Not allowed

- A full or substantial GenAI solution for this individual lab
- Fabricated results, tests, or citations
- Prompts meant to hide assistance
- Pasting exam, personal, or third-party confidential / proprietary material into tools. Your **own** lab in a local IDE agent / Copilot is allowed.

### Minimum if you used AI

Same fields as the project **AI use** slice. Fill them in **REPORT.md**. “I used ChatGPT” is not enough.

| Field | What to write |
| --- | --- |
| **Tools / skills** | Named GenAI and agent tools |
| **Purpose** | What you asked the tool to do (a step, not “do the lab”) |
| **Representative prompts** | Short examples, or an appendix |
| **Affected files/sections** | Where assistance landed |
| **Validation steps** | What you ran (`./gradlew check`) and what you changed |
| **Citations** | External snippets you adapted |
| **Human-reviewed** | What you kept, edited, or rejected |

If you used no AI, write **No AI assistance**.

We do **not** use AI detectors. We may look at commits and **REPORT.md**.
