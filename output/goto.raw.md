---
marp: true
title: Gen AI Grows Up: Building Production-Ready Agents on the JVM
theme: default
paginate: false
class: invert
size: 16:9
---

<!-- _class: invert -->
# Gen AI Grows Up
## Building Production-Ready Agents on the JVM

![bg right:30%](https://raw.githubusercontent.com/embabel/embabel-agent/refs/heads/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg)

### Rod Johnson, Embabel
Creator of Spring and Embabel
GOTO Copenhagen 2025

---

<!-- _header: Personal AI Assistants: The Success Story -->

# The Gen AI Revolution: Personal Success

- **Amazon Alexa+**: Proactively manages schedules, sends timely updates
- **Canva AI**: Combines conversation with design generation
- **Julius AI**: Enhances data analysis and visualization

Estimates show:
- **26%** of workers use generative AI professionally
- **1/3** of adults use AI personally outside work

![bg right:40%](https://imgs.search.brave.com/L6R1_wTXaS_vKSH0ZtuxLADtnMwE8_V2wZZWJN7HYxA/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS80/R3pMblpxM0JyQS9t/YXhyZXNkZWZhdWx0/LmpwZw)

---

<!-- _header: The Harsh Reality in Business -->

# But Enterprise Projects Are Failing

## Shocking Statistics

- **MIT**: 95% of AI pilots fail to deliver business returns
- **Gartner**: 40% of agentic AI projects will be canceled by 2027
- **Gartner**: 30% of generative AI projects abandoned post-POC by end of 2025

> "The 95% failure rate for enterprise AI solutions represents the clearest indicator that we need to rethink our approach" – MIT Report 2025

---

<!-- _header: Why AI Projects Fail -->

# Why Are Enterprise AI Projects Failing?

1. **Technology is nondeterministic** - unpredictable behavior, hard to test
2. **Rapid evolution** - tools and best practices changing constantly
3. **High costs** - expensive to run, escalating implementation expenses
4. **Integration challenges** - disconnected from existing systems
5. **Python frameworks misalignment** - not built for enterprise reliability
6. **Vendor-driven solutions** - selling quick fixes without long-term value

![bg right:30% brightness:0.9](https://miro.medium.com/v2/resize:fit:1400/1*m0H6-tUbW6grMlezlb52yw.png)

---

<!-- _header: The Nondeterminism Problem -->

# The Unpredictability Challenge

## Nondeterminism in AI Systems

LLM outputs vary based on:
- Temperature settings
- Model versions
- Context window contents
- Prompt formulations

> **Business-critical applications need AI agents to behave predictably to maintain trust and ensure operational stability.**

---

<!-- _header: Integration Failure -->

# The Integration Gap

## Misaligned with Business Reality

- **Technical complexity**: Coordinating between legacy systems and AI components
- **Organizational hurdles**: Trust, judgment, coordination across departments
- **Python frameworks limitations**:
  - Lack robust typing support
  - Poor runtime enforcement
  - Integration difficulties with enterprise solutions

> Many Python developers are not accustomed to writing typings, leading to fragile and error-prone code in business environments

---

<!-- _header: The Vendor Problem -->

# The Vendor Snake Oil Problem

- **FTC warnings**: Vendors often oversell AI capabilities
- **AI washing**: Loose use of the term "AI" to mislead
- **Opaque technology**: Marketing hype rather than transparent capabilities
- **Hidden costs**: Implementation expenses drastically exceed initial quotes
- **Lock-in risks**: Proprietary solutions that create dependencies

> "Up to 70% of current AI agent tasks are performed incorrectly" – Carnegie Mellon University study

---

<!-- _header: A Better Approach -->

# How Do We Fix This?

1. **Attack nondeterminism**: Make agents as predictable as possible
   - Strong typing
   - Deterministic planning
   - Testable components

2. **Integrate with existing systems**: Don't reinvent the wheel
   - Build on enterprise platforms
   - Leverage domain models
   - Connect to systems of record

---

<!-- _header: The Java Developer's Choice -->

# What's the Role of Java Developers?

## Two Paths Forward:

1. **Imitate Python** - Doomed to play catch-up with the same problems
   - If this is our best, go learn Python
   
2. **Leverage JVM strengths** - Create something better
   - Strong typing
   - Enterprise ecosystem
   - Production reliability
   - Integration capabilities

---

<!-- _header: JVM Advantage -->

# The Future of Gen AI for Business is on the JVM

![bg right:35%](https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg)

## Java's Enterprise Advantages

- **Performance**: Optimized for production workloads
- **Robustness**: Type-safety, mature error handling
- **Scalability**: Proven in mission-critical systems
- **Security**: Built-in features, enterprise-grade
- **Integration**: Seamless with existing systems
- **Tooling**: Comprehensive monitoring, debugging

---

<!-- _header: Introducing Embabel -->

# Introducing Embabel

![bg left:30%](https://raw.githubusercontent.com/embabel/embabel-agent/refs/heads/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg)

## A New Agent Framework for the JVM

> "Not since I founded the Spring Framework have I been so convinced that a new project is needed." – Rod Johnson

Embabel is a framework for authoring agentic flows on the JVM that seamlessly mix LLM-prompted interactions with code and domain models.

---

<!-- _header: Embabel's Innovation -->

# How Embabel Addresses the Challenges

## Unique Planning Element

Embabel introduces **Goal-Oriented Action Planning (GOAP)**:

- Smart but deterministic planning algorithm from gaming AI
- Actions and Goals allow extensible system
- More predictable than Finite State Machines
- Always produces the same plan given the same inputs

> Unlike other frameworks that rely solely on LLMs directly, Embabel uses a pluggable planning step for predictability

---

<!-- _header: Strong Typing -->

# Structure Over Chaos

![bg right:40%](https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Kotlin_Icon.svg/1024px-Kotlin_Icon.svg.png?20171012085709)

## Embabel Brings Structure to LLM Interactions

- **Strong typing system** using Kotlin/Java
- **Inference of conditions** from data flow 
- **Compile-time safety** reduces runtime errors
- **Domain model integration** for business alignment

Compare to Python frameworks like Crew AI, which use untyped text exchanges

---

<!-- _header: Goal-Oriented Action Planning -->

# GOAP: The Secret Sauce

```kotlin
interface Action {
    fun getCost(): Double
    fun getPreconditions(): List<Condition>
    fun getPostconditions(): List<Condition>
}
```

- **Actions**: Defined with preconditions and postconditions
- **Goals**: Target states the agent aims to achieve
- **Planning**: Intelligent path-finding toward goals
- **Adaptation**: Re-plans based on changing conditions

> This approach enables adaptive behavior without sacrificing robust control over the execution flow

---

<!-- _header: Embabel Architecture -->

# Embabel's Architecture

dot digraph EmbabelArchitecture {
    rankdir=TB;
    node [shape=box, style=filled, color=lightblue];
    edge [color=gray];
    
    Agent [label="Agent"];
    Goals [label="Goals"];
    Actions [label="Actions"];
    Planner [label="GOAP Planner", color=gold];
    DomainModel [label="Domain Model"];
    Spring [label="Spring Integration", color=lightgreen];
    LLM [label="LLM Interactions"];
    
    Agent -> Goals;
    Agent -> Planner;
    Goals -> Planner;
    Actions -> Planner;
    DomainModel -> Actions;
    DomainModel -> Goals;
    Planner -> LLM;
    Spring -> Agent;
    Spring -> DomainModel;
}

---

<!-- _header: Spring Integration -->

# Seamless Spring Integration

![bg right:30%](https://upload.wikimedia.org/wikipedia/commons/4/44/Spring_Framework_Logo_2018.svg)

## Embabel + Spring = Enterprise Ready

- **@Actions** and **@Goals** annotations
- **Spring Bean** discovery
- **Dependency Injection** for components
- **Spring AI** integration for model access
- **Configuration Properties** for easy setup

---

<!-- _header: Execution Modes -->

# Flexible Execution Modes

## Balance Determinism and Creativity

1. **Focused Mode**: Highly deterministic, limited LLM use
2. **Closed Mode**: Balanced, uses LLM for specific tasks
3. **Open Mode**: Most capable but less deterministic

> Embabel lets you choose the right balance of determinism vs. flexibility for your use case

---

<!-- _header: Testing and Reliability -->

# Designed for Testability

## Enterprise-Grade Reliability

- **Unit tests** for individual actions
- **Integration tests** for entire workflows
- **Mock LLM responses** for deterministic testing
- **Hyperparameter validation** to ensure consistency
- **Plan inspection** before execution

> Unlike most AI frameworks, Embabel prioritizes testing to ensure reliability despite the intrinsic nondeterminism of AI models

---

<!-- _header: Business Integration -->

# Bridging AI and Business Logic

## Seamless Connection to Enterprise Systems

Embabel:
- **Discovers** actions and goals from existing code
- **Maps** AI interactions onto business logic
- **Leverages** existing domain models
- **Minimizes** disruption to existing systems
- **Maintains** enterprise robustness requirements

> AI agents operate as first-class citizens within business systems rather than as awkward add-ons

---

<!-- _header: Real-World Example: Decker -->

# Real-World Example: Decker

## This Presentation Was Created by Embabel!

Decker is an Embabel agent that:
1. Starts from a YAML definition of presentation goals
2. Performs internet research using different models in parallel
3. Extracts code examples from software projects
4. Generates Markdown slides adhering to Marp conventions
5. Converts Markdown to HTML presentation
6. Expands diagrams using GraphViz

---

<!-- _header: Decker's Flow -->

# How Decker Works

dot digraph DeckerFlow {
    rankdir=LR;
    node [shape=box, style=filled, color=lightblue];
    edge [color=gray];
    
    YAML [label="YAML Definition"];
    Research [label="Internet Research"];
    Planning [label="Content Planning", color=gold];
    Generation [label="Markdown Generation"];
    Conversion [label="HTML Conversion"];
    Graphics [label="GraphViz Expansion"];
    
    YAML -> Planning;
    Planning -> Research;
    Research -> Generation;
    Planning -> Generation;
    Generation -> Conversion;
    Generation -> Graphics;
    Graphics -> Conversion;
}

---

<!-- _header: Decker in Embabel -->

# Decker: Goal-Oriented Implementation

## Key Components:

- **Goals**: CreatePresentation, ResearchTopics, GenerateSlides
- **Actions**: ParseYaml, WebSearch, GenerateContent, ConvertToHtml
- **Domain Model**: PresentationRequest, SlideContent, ImageInfo

> Decker demonstrates how Embabel's GOAP enables complex workflows with a mix of LLM prompting and code execution

---

<!-- _header: Python vs Java Implementation -->

# Python vs. Java Agent Implementation

## Python Agent (Untyped, Error-Prone)

```python
def research_agent(topic):
    # Untyped, any string can be passed
    results = llm.generate(f"Research {topic}")
    return results  # Could be any format
```

## Java/Kotlin Agent (Typed, Reliable)

```kotlin
@Action
fun researchTopic(topic: ResearchTopic): ResearchResults {
    // Type-safe, structured input and output
    return llmService.generate(PromptTemplate.research, topic)
}
```

---

<!-- _header: Success Metrics -->

# Embabel Success Metrics

## What Makes Embabel Superior

- **Predictability**: Deterministic planning reduces unexpected behavior
- **Extensibility**: Add new actions without modifying existing code
- **Integration**: Works with your enterprise Java/Spring ecosystem
- **Performance**: JVM optimization for production workloads
- **Testing**: Comprehensive testing strategy for reliability
- **Maintainability**: Strong typing reduces runtime errors

---

<!-- _header: Spring AI Integration -->

# Spring AI + Embabel: Perfect Match

![bg right:30%](https://raw.githubusercontent.com/spring-io/spring-io-static/refs/heads/main/blog/tzolov/20250520/spring-ai-logo.png)

## Leveraging Spring AI

- **Model abstraction** across providers
- **Prompt engineering** capabilities
- **Token management** and optimization
- **Multiple model support** in one application
- **Enterprise security** features

---

<!-- _header: Beyond Framework Limitations -->

# Breaking Free from Framework Limitations

## Emerging Best Practices

1. **Start small**: Narrowly defined use cases
2. **Iterative improvement**: Continuous testing and assessment
3. **Multidisciplinary teams**: Agile collaboration
4. **Clear metrics**: Defined business impact measures
5. **Balanced validation**: Automated and human-in-the-loop

> Embabel supports all these best practices with its architecture

---

<!-- _header: Getting Started -->

# Getting Started with Embabel

## java-agent-template

```bash
# Clone the template repository
git clone https://github.com/embabel/java-agent-template.git

# Or use the project creator
uvx --from git+https://github.com/embabel/project-creator.git project-creator
```

- Built-in Spring Shell for easy interaction
- Example agents with different capabilities
- Unit tests for verifying prompts and hyperparameters
- Ready for customization with your domain model

---

<!-- _header: Choose Your Path -->

# Choose Your Path Forward

## The Choice is Clear

1. **Vendor Snake Oil**: High cost, high risk
2. **Python Frameworks**: Not designed for enterprise
3. **Embabel**: The best agent framework anywhere

> "Once more the Java community can take back control from vendors and demonstrate what works" – Rod Johnson

---

<!-- _header: Call to Action -->

# Call to Action

![bg right:30%](https://raw.githubusercontent.com/embabel/embabel-agent/refs/heads/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg)

## Start Your Gen AI Journey Today

1. Put a little Gen AI into your apps today
2. Grow towards unlocking your full business value
3. Try Embabel - the framework that brings Gen AI to your enterprise

**Visit**: [github.com/embabel/embabel-agent](https://github.com/embabel/embabel-agent)

---

<!-- _header: References -->

# References

1. [Embabel GitHub Repository](https://github.com/embabel/embabel-agent)
2. [Embabel: A New Agent Platform For the JVM](https://medium.com/@springrod/embabel-a-new-agent-platform-for-the-jvm-1c83402e0014)
3. [The Embabel Vision](https://medium.com/@springrod/the-embabel-vision-967654f13793)
4. [AI for your Gen AI: How and Why Embabel Plans](https://medium.com/@springrod/ai-for-your-gen-ai-how-and-why-embabel-plans-3930244218f6)
5. [From Alchemy to Engineering: Building Type-Safe Gen AI](https://medium.com/@springrod/from-alchemy-to-engineering-building-type-safe-gen-ai-applications-with-embabel-c3d89b7c989f)
6. [MIT Report: 95% of AI Pilots Failing](https://fortune.com/2025/08/18/mit-report-95-percent-generative-ai-pilots-at-companies-failing-cfo/)
7. [Gartner: 30% of GenAI Projects Abandoned](https://www.gartner.com/en/newsroom/press-releases/2024-07-29-gartner-predicts-30-percent-of-generative-ai-projects-will-be-abandoned-after-proof-of-concept-by-end-of-2025)
8. [Gartner: 40% of Agentic AI Projects Canceled](https://www.gartner.com/en/newsroom/press-releases/2025-06-25-gartner-predicts-over-40-percent-of-agentic-ai-projects-will-be-canceled-by-end-of-2027)
