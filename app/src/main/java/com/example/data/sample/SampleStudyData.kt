package com.example.data.sample

import com.example.data.model.FlashcardItem
import com.example.data.model.NoteCategory
import com.example.data.model.StudyDocument
import com.example.data.model.StudyFolder
import com.example.data.model.StudyNote

object SampleStudyData {

    val sampleFolders = listOf(
        StudyFolder(
            id = "physics",
            name = "Quantum Physics",
            description = "Relativity, Quantum Mechanics, Thermodynamics",
            colorHex = 0xFF6366F1,
            iconName = "Bolt",
            docCount = 3
        ),
        StudyFolder(
            id = "chemistry",
            name = "Organic Chemistry",
            description = "Reactions, Mechanisms & Spectroscopy",
            colorHex = 0xFF06B6D4,
            iconName = "Science",
            docCount = 2
        ),
        StudyFolder(
            id = "math",
            name = "Calculus & Math",
            description = "Differential Equations, Linear Algebra",
            colorHex = 0xFFA855F7,
            iconName = "Functions",
            docCount = 2
        ),
        StudyFolder(
            id = "cs_ai",
            name = "AI & Computer Science",
            description = "Neural Nets, Transformers, Algorithms",
            colorHex = 0xFF10B981,
            iconName = "Memory",
            docCount = 3
        ),
        StudyFolder(
            id = "history",
            name = "History & General",
            description = "Modern History, Philosophy, Law",
            colorHex = 0xFFF59E0B,
            iconName = "MenuBook",
            docCount = 1
        )
    )

    val sampleDocuments = listOf(
        StudyDocument(
            id = "doc_quantum_physics",
            title = "Quantum Physics & General Relativity",
            subject = "Quantum Physics",
            folderId = "physics",
            isBundledSample = true,
            sampleType = "physics",
            totalPages = 8,
            lastReadPage = 3,
            lastReadTimestamp = System.currentTimeMillis() - 1000 * 60 * 35,
            totalReadingMinutes = 48,
            isFavorite = true,
            tags = listOf("Quantum", "Relativity", "Exam Prep", "Formulas"),
            notesCount = 5,
            bookmarks = listOf(1, 3, 5),
            estimatedReadTimeMinutes = 25,
            aiSummaryPoints = listOf(
                "Wave-Particle Duality: Matter exhibits both particulate and wave-like characteristics governed by de Broglie wavelength λ = h/p.",
                "Schrödinger Wave Equation: Dictates the time-evolution of probability amplitudes across Hilbert space.",
                "Heisenberg Uncertainty: Fundamental physical limit Δx · Δp ≥ ℏ/2 prevents simultaneous exact measurement.",
                "Spacetime Curvature: Einstein's Field Equations G_μν + Λg_μν = (8πG/c⁴) T_μν describe gravity as geometric curvature."
            )
        ),
        StudyDocument(
            id = "doc_organic_chem",
            title = "Organic Chemistry Synthesis & Mechanisms",
            subject = "Organic Chemistry",
            folderId = "chemistry",
            isBundledSample = true,
            sampleType = "chem",
            totalPages = 6,
            lastReadPage = 2,
            lastReadTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
            totalReadingMinutes = 32,
            isFavorite = true,
            tags = listOf("Synthesis", "Reactions", "Mechanisms", "SN1/SN2"),
            notesCount = 4,
            bookmarks = listOf(2, 4),
            estimatedReadTimeMinutes = 20,
            aiSummaryPoints = listOf(
                "SN1 vs SN2: SN1 is unimolecular via planar carbocation intermediate (racemization); SN2 is concerted backside attack with Walden inversion.",
                "Aromaticity & Hückel's Rule: Cyclic planar conjugated systems require [4n + 2] π-electrons for special aromatic stability.",
                "Aldol & Claisen Condensations: Key C-C bond forming reactions utilizing enolate nucleophiles attacking carbonyl electrophiles."
            )
        ),
        StudyDocument(
            id = "doc_calculus_diff_eq",
            title = "Multivariable Calculus & Differential Systems",
            subject = "Calculus & Math",
            folderId = "math",
            isBundledSample = true,
            sampleType = "math",
            totalPages = 7,
            lastReadPage = 1,
            lastReadTimestamp = System.currentTimeMillis() - 1000 * 60 * 360,
            totalReadingMinutes = 18,
            isFavorite = false,
            tags = listOf("Calculus", "Vectors", "Laplace", "PDEs"),
            notesCount = 3,
            bookmarks = listOf(1),
            estimatedReadTimeMinutes = 30,
            aiSummaryPoints = listOf(
                "Gradient, Divergence & Curl: Fundamental vector calculus operators governing fluid flow and Maxwell's electrodynamics.",
                "Green's, Stokes' & Gauss' Divergence Theorems: Bridge line integrals, surface fluxes, and volume integrals.",
                "Laplace Transform: Converts differential equations into algebraic polynomials in the s-domain for rapid solving."
            )
        ),
        StudyDocument(
            id = "doc_ai_deep_learning",
            title = "Transformer Architectures & Deep Learning",
            subject = "AI & Computer Science",
            folderId = "cs_ai",
            isBundledSample = true,
            sampleType = "ai",
            totalPages = 10,
            lastReadPage = 4,
            lastReadTimestamp = System.currentTimeMillis() - 1000 * 60 * 10,
            totalReadingMinutes = 65,
            isFavorite = true,
            tags = listOf("Neural Nets", "Attention", "LLMs", "Optimization"),
            notesCount = 6,
            bookmarks = listOf(1, 4, 7),
            estimatedReadTimeMinutes = 35,
            aiSummaryPoints = listOf(
                "Scaled Dot-Product Attention: Attention(Q,K,V) = softmax(QKᵀ / √d_k) V calculates dynamic token context.",
                "Multi-Head Mechanism: Allows joint attention over multiple representation subspaces simultaneously.",
                "Layer Normalization & Residuals: Stabilize deep gradient propagation across hundreds of transformer layers."
            )
        ),
        StudyDocument(
            id = "doc_world_history",
            title = "Modern World History: 1945 to Present",
            subject = "History & General",
            folderId = "history",
            isBundledSample = true,
            sampleType = "history",
            totalPages = 5,
            lastReadPage = 1,
            lastReadTimestamp = System.currentTimeMillis() - 1000 * 60 * 1440,
            totalReadingMinutes = 15,
            isFavorite = false,
            tags = listOf("History", "Geopolitics", "Cold War", "Economics"),
            notesCount = 2,
            bookmarks = listOf(1),
            estimatedReadTimeMinutes = 15,
            aiSummaryPoints = listOf(
                "Post-WWII Global Realignment: Division into Eastern and Western blocs, NATO vs Warsaw Pact.",
                "Decolonization Wave: Rapid emergence of sovereign independent nations across Asia and Africa in the 1950s-1970s.",
                "The Digital Information Age: Transition from industrial economy to interconnected knowledge society."
            )
        )
    )

    val sampleNotes = listOf(
        StudyNote(
            id = "note_1",
            documentId = "doc_quantum_physics",
            pageNumber = 1,
            text = "Remember: Photoelectric effect proves light acts as quantized packets (photons) with energy E = hν. Einstein won the 1921 Nobel Prize for this!",
            highlightedSnippet = "E = hν = ℏω",
            category = NoteCategory.EXAM_PREP
        ),
        StudyNote(
            id = "note_2",
            documentId = "doc_quantum_physics",
            pageNumber = 2,
            text = "Schrödinger time-independent form: Ĥψ = Eψ. The Hamiltonian operator represents the total kinetic + potential energy.",
            highlightedSnippet = "Ĥψ = Eψ",
            category = NoteCategory.FORMULA
        ),
        StudyNote(
            id = "note_3",
            documentId = "doc_quantum_physics",
            pageNumber = 3,
            text = "Quantum Tunneling occurs because wavefunctions decay exponentially inside potential barriers instead of instantly dropping to zero.",
            highlightedSnippet = "ψ(x) ~ e^(-αx)",
            category = NoteCategory.CORE_CONCEPT
        ),
        StudyNote(
            id = "note_4",
            documentId = "doc_organic_chem",
            pageNumber = 2,
            text = "Polar aprotic solvents (like DMSO, Acetone, DMF) accelerate SN2 reactions by leaving the nucleophile naked and reactive.",
            highlightedSnippet = "SN2 solvent preference: Polar aprotic",
            category = NoteCategory.EXAM_PREP
        ),
        StudyNote(
            id = "note_5",
            documentId = "doc_ai_deep_learning",
            pageNumber = 4,
            text = "Why divide by √d_k in attention? For large dimensions, dot products grow large, pushing softmax into regions with vanishing gradients!",
            highlightedSnippet = "Softmax scaling factor: 1/√d_k",
            category = NoteCategory.CORE_CONCEPT
        )
    )

    val sampleFlashcards = listOf(
        FlashcardItem(
            id = "fc_1",
            documentId = "doc_quantum_physics",
            documentTitle = "Quantum Physics & General Relativity",
            question = "What is the physical meaning of the squared absolute wavefunction |ψ(x,t)|²?",
            answer = "According to Max Born's probabilistic interpretation, |ψ(x,t)|² represents the probability density of finding the particle at position x at time t.",
            subject = "Quantum Physics",
            pageNumber = 1,
            confidenceScore = 2
        ),
        FlashcardItem(
            id = "fc_2",
            documentId = "doc_quantum_physics",
            documentTitle = "Quantum Physics & General Relativity",
            question = "State Heisenberg's Uncertainty Principle for position and momentum.",
            answer = "Δx · Δp ≥ ℏ / 2 (where ℏ = h / 2π). You cannot simultaneously know both exact position and exact momentum of a quantum particle.",
            subject = "Quantum Physics",
            pageNumber = 2,
            confidenceScore = 3
        ),
        FlashcardItem(
            id = "fc_3",
            documentId = "doc_organic_chem",
            documentTitle = "Organic Chemistry Synthesis & Mechanisms",
            question = "What are the four conditions required for a molecule to be aromatic under Hückel's Rule?",
            answer = "1. Cyclic structure\n2. Fully conjugated\n3. Planar geometry\n4. Contains (4n + 2) π-electrons, where n is a non-negative integer.",
            subject = "Organic Chemistry",
            pageNumber = 2,
            confidenceScore = 1
        ),
        FlashcardItem(
            id = "fc_4",
            documentId = "doc_calculus_diff_eq",
            documentTitle = "Multivariable Calculus & Differential Systems",
            question = "What does the divergence ∇ · F of a vector field measure physically?",
            answer = "It measures the net rate of flux outward from an infinitesimal volume around a point (source if > 0, sink if < 0, solenoidal/incompressible if = 0).",
            subject = "Calculus & Math",
            pageNumber = 3,
            confidenceScore = 2
        ),
        FlashcardItem(
            id = "fc_5",
            documentId = "doc_ai_deep_learning",
            documentTitle = "Transformer Architectures & Deep Learning",
            question = "What is the formula for Scaled Dot-Product Attention in Transformers?",
            answer = "Attention(Q, K, V) = softmax((Q · Kᵀ) / √d_k) · V",
            subject = "AI & Computer Science",
            pageNumber = 4,
            confidenceScore = 3
        ),
        FlashcardItem(
            id = "fc_6",
            documentId = "doc_ai_deep_learning",
            documentTitle = "Transformer Architectures & Deep Learning",
            question = "What is the key purpose of Positional Encodings in Transformer models?",
            answer = "Because self-attention operations are permutation-invariant (order-agnostic), positional encodings inject sequential token position information into input embeddings.",
            subject = "AI & Computer Science",
            pageNumber = 2,
            confidenceScore = 0
        )
    )

    // Detailed Study Content pages for rich page rendering & read-aloud TTS
    fun getDocumentPageContent(sampleType: String?, pageNumber: Int): PageStudyContent {
        return when (sampleType) {
            "physics" -> getPhysicsPage(pageNumber)
            "chem" -> getChemPage(pageNumber)
            "math" -> getMathPage(pageNumber)
            "ai" -> getAiPage(pageNumber)
            "history" -> getHistoryPage(pageNumber)
            else -> PageStudyContent(
                pageNumber = pageNumber,
                title = "Study Document - Page $pageNumber",
                subtitle = "Active Study Reference",
                sections = listOf(
                    PageSection("Overview", "This is an imported or synthesized study document page. You can annotate, bookmark, practice flashcards, and activate 3D book flip mode."),
                    PageSection("Study Takeaway", "Focus on key concepts and take structured timestamped notes to reinforce long-term memory.")
                ),
                keyFormula = "Knowledge = Consistency × Focus",
                examTips = listOf("Review this page before the scheduled Pomodoro break.")
            )
        }
    }

    private fun getPhysicsPage(page: Int): PageStudyContent {
        return when (page) {
            1 -> PageStudyContent(
                pageNumber = 1,
                title = "Chapter 1: The Quantum Foundation & Photons",
                subtitle = "Wave-Particle Duality & Planck's Quantum Hypothesis",
                sections = listOf(
                    PageSection(
                        "1.1 Blackbody Radiation & The Ultraviolet Catastrophe",
                        "Classical Rayleigh-Jeans theory predicted infinite radiation energy at high frequencies. Max Planck resolved this in 1900 by hypothesizing that energy is emitted and absorbed only in discrete packets called quanta:\n\nE = n · h · ν\n\nwhere h = 6.626 × 10⁻³⁴ J·s is Planck's constant and ν is frequency."
                    ),
                    PageSection(
                        "1.2 Einstein's Photoelectric Effect (1905)",
                        "Light behaves as an ensemble of localized energy packets (photons). When a photon collides with an electron on a metallic surface:\n\nK_max = hν - Φ\n\nHere, Φ is the work function (minimum ionization energy). Emission is instantaneous and independent of light intensity, depending solely on frequency ν > ν_threshold."
                    )
                ),
                keyFormula = "E = hν = (h · c) / λ   |   K_max = e · V_stop",
                examTips = listOf(
                    "Work function Φ is metal-specific.",
                    "Increasing intensity increases photocurrent, NOT kinetic energy."
                )
            )
            2 -> PageStudyContent(
                pageNumber = 2,
                title = "Chapter 2: The Schrödinger Wave Equation",
                subtitle = "State Vectors in Hilbert Space & Operators",
                sections = listOf(
                    PageSection(
                        "2.1 Time-Dependent Schrödinger Equation (TDSE)",
                        "The fundamental equation describing non-relativistic quantum systems:\n\niℏ · ∂ψ(r,t)/∂t = Ĥ ψ(r,t)\n\nĤ is the Hamiltonian operator: Ĥ = -(ℏ²/2m)∇² + V(r,t). The complex wavefunction ψ(r,t) contains all measurable physical information about the state."
                    ),
                    PageSection(
                        "2.2 Probability Current Density & Normalization",
                        "Since the total probability of finding the particle across all space is 1:\n\n∫ |ψ(r,t)|² d³r = 1\n\nThe probability continuity equation satisfies ∂ρ/∂t + ∇ · J = 0, ensuring conservation of probability."
                    )
                ),
                keyFormula = "Ĥψ = Eψ   |   Ĥ = - (ℏ² / 2m) d²/dx² + V(x)",
                examTips = listOf(
                    "Wavefunction ψ must be continuous, single-valued, and square-integrable.",
                    "Energies of bound states are strictly quantized."
                )
            )
            3 -> PageStudyContent(
                pageNumber = 3,
                title = "Chapter 3: Quantum Tunneling & Potential Barriers",
                subtitle = "Overcoming Classical Energy Bounds",
                sections = listOf(
                    PageSection(
                        "3.1 Finite Potential Barrier Penetration",
                        "When a particle with energy E < V₀ encounters a potential step, classical mechanics forbids entry. However, the quantum wave equation inside the barrier yields:\n\nψ(x) = C e^(-κx) + D e^(+κx), where κ = √[2m(V₀ - E)] / ℏ\n\nThe non-zero exponential tail penetrates the barrier, leading to a finite Transmission Coefficient T > 0."
                    ),
                    PageSection(
                        "3.2 Real-World Applications",
                        "• Scanning Tunneling Microscopes (STM) measuring atomic surfaces\n• Alpha radioactive decay in heavy atomic nuclei\n• Nuclear fusion inside stellar cores at lower temperatures than classical ignition"
                    )
                ),
                keyFormula = "T ≈ 16(E/V₀)(1 - E/V₀) · e^(-2κL)",
                examTips = listOf(
                    "Transmission probability drops exponentially with barrier width L.",
                    "Lighter particles (like electrons) tunnel far more readily than heavy nucleons."
                )
            )
            else -> PageStudyContent(
                pageNumber = page,
                title = "Chapter $page: Relativistic Spacetime & Field Equations",
                subtitle = "Lorentz Transformations & Gravitational Geometry",
                sections = listOf(
                    PageSection(
                        "Spacetime Metric Tensor",
                        "The invariant spacetime interval ds² = g_μν dx^μ dx^ν unites space and time into a 4-dimensional Riemannian manifold. Geodesic equations govern free-fall trajectories under zero net proper acceleration."
                    ),
                    PageSection(
                        "Einstein's Field Equations",
                        "Matter and energy tell spacetime how to curve, and curved spacetime tells matter how to move:\n\nG_μν = (8πG / c⁴) · T_μν"
                    )
                ),
                keyFormula = "ds² = -c²dt² + dx² + dy² + dz²",
                examTips = listOf("Light follows null geodesics with ds² = 0.")
            )
        }
    }

    private fun getChemPage(page: Int): PageStudyContent {
        return PageStudyContent(
            pageNumber = page,
            title = "Organic Synthesis Module - Section $page",
            subtitle = if (page == 1) "Nucleophilic Substitution & Elimination" else "Aromatic Electrophilic Substitution & Carbonyls",
            sections = listOf(
                PageSection(
                    "Reaction Pathways & Transition States",
                    "• SN2: Bimolecular, 1 step, concerted backside nucleophilic attack. Rate = k[substrate][Nu]. Requires low steric hindrance (1° > 2° >> 3°).\n• SN1: Unimolecular, 2 steps, carbocation intermediate. Rate = k[substrate]. Stability: 3° > 2° > 1°."
                ),
                PageSection(
                    "Regioselectivity & Stereochemistry",
                    "Zaitsev's Rule favors more substituted, thermodynamically stable alkene products in E2/E1 reactions, while bulky bases (e.g. KOtBu) direct toward Hofmann kinetic products."
                )
            ),
            keyFormula = "Rate = k[R-X][Nu⁻]  (SN2)  |  Rate = k[R-X]  (SN1)",
            examTips = listOf(
                "Good leaving groups: I⁻ > Br⁻ > Cl⁻ >> F⁻ (conjugate bases of strong acids).",
                "Polar protic solvents favor SN1; Polar aprotic solvents favor SN2."
            )
        )
    }

    private fun getMathPage(page: Int): PageStudyContent {
        return PageStudyContent(
            pageNumber = page,
            title = "Vector Calculus & Differential Operators - Page $page",
            subtitle = "Field Theorems & Partial Differential Equations",
            sections = listOf(
                PageSection(
                    "The Del (Nabla) Operator ∇",
                    "In Cartesian coordinates (x,y,z):\n• Gradient ∇f = (∂f/∂x)i + (∂f/∂y)j + (∂f/∂z)k (points in direction of steepest ascent)\n• Divergence ∇ · F = ∂F_x/∂x + ∂F_y/∂y + ∂F_z/∂z (scalar measure of source/sink)\n• Curl ∇ × F = det matrix of spatial derivatives (circulation density)"
                ),
                PageSection(
                    "Stokes' Circulation Theorem",
                    "∮_C F · dr = ∬_S (∇ × F) · dS\nRelates the microscopic curl across an oriented surface S to the macroscopic line integral circulation around boundary curve C."
                )
            ),
            keyFormula = "∯_S F · dS = ∭_V (∇ · F) dV   (Gauss' Divergence Theorem)",
            examTips = listOf(
                "If ∇ × F = 0, the field is conservative and path-independent.",
                "Laplacian operator Δ = ∇² = ∂²/∂x² + ∂²/∂y² + ∂²/∂z²."
            )
        )
    }

    private fun getAiPage(page: Int): PageStudyContent {
        return PageStudyContent(
            pageNumber = page,
            title = "Deep Learning & Transformer Mechanics - Page $page",
            subtitle = "Self-Attention, KV Caching & Generative Models",
            sections = listOf(
                PageSection(
                    "Multi-Head Self-Attention Architecture",
                    "Input embeddings X are projected via learned weight matrices W_Q, W_K, W_V into Queries, Keys, and Values:\n\nQ = X·W_Q,  K = X·W_K,  V = X·W_V\n\nMultiHead(Q,K,V) = Concat(head_1, ..., head_h) W_O\nEach head can attend to syntactic, semantic, or long-range dependencies independently."
                ),
                PageSection(
                    "Transformer Block Composition",
                    "1. Input + Positional Encoding\n2. Multi-Head Attention + Residual Connection + LayerNorm\n3. Position-Wise Feed-Forward Network (FFN with SwiGLU / GeLU)\n4. Residual Connection + LayerNorm"
                )
            ),
            keyFormula = "Attention(Q,K,V) = softmax( (Q · Kᵀ) / √d_k ) · V",
            examTips = listOf(
                "KV caching reuses previously computed K and V tokens during auto-regressive generation, changing inference complexity from O(N²) to O(N).",
                "Rotary Position Embeddings (RoPE) encode relative position as rotation in complex 2D subspace."
            )
        )
    }

    private fun getHistoryPage(page: Int): PageStudyContent {
        return PageStudyContent(
            pageNumber = page,
            title = "Modern World History & Strategy - Page $page",
            subtitle = "Post-War Diplomacy, Geopolitics & Institutional Frameworks",
            sections = listOf(
                PageSection(
                    "The Bretton Woods System & Reconstruction",
                    "Established in 1944 to prevent competitive currency devaluations and promote global economic reconstruction, creating the International Monetary Fund (IMF) and International Bank for Reconstruction and Development (World Bank)."
                ),
                PageSection(
                    "Strategic Doctrines of the Cold War",
                    "• Containment Doctrine (George Kennan's Long Telegram, 1946)\n• Marshall Plan (European Recovery Program, 1948)\n• Mutually Assured Destruction (MAD) nuclear deterrence stability"
                )
            ),
            keyFormula = "Strategic Stability = Deterrence × Credible Resolve",
            examTips = listOf(
                "Key turning points: Berlin Airlift (1948), Cuban Missile Crisis (1962), Helsinki Accords (1975)."
            )
        )
    }
}

data class PageStudyContent(
    val pageNumber: Int,
    val title: String,
    val subtitle: String,
    val sections: List<PageSection>,
    val keyFormula: String,
    val examTips: List<String>
)

data class PageSection(
    val header: String,
    val body: String
)
