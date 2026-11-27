package com.example.productexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.productexplorer.ui.theme.ProductExplorerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductExplorerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileContainer(
                        onContactClick = {
                            // Plus tard : contacter le développeur
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class DeveloperUi(
    val name: String,
    val role: String,
    val description: String,
    val email: String,
    val location: String,
    val availability: String
)

data class SkillUi(
    val id: Int,
    val name: String
)

data class ProjectUi(
    val id: Int,
    val title: String,
    val description: String,
    val technologies: String
)

fun sampleDeveloper(): DeveloperUi {
    return DeveloperUi(
        name = "Maxime BEAUJARD",
        role = "Alternant en Architecture Logicielle",
        description = "En alternance, je cherche à renforcer mes bases en développement mobile avec Jetpack Compose.",
        email = "maxime@exemple.com",
        location = "Tours",
        availability = "Disponible pour une alternance"
    )
}

fun sampleSkills(): List<SkillUi> {
    return listOf(
        SkillUi(1, "Kotlin"),
        SkillUi(2, "Jetpack Compose"),
        SkillUi(3, "Git"),
        SkillUi(4, "Material 3"),
        SkillUi(5, "Java")
    )
}

fun sampleProjects(): List<ProjectUi> {
    return listOf(
        ProjectUi(
            id = 1,
            title = "Product Explorer",
            description = "Catalogue de produits local avec écran d'accueil, détail et thème clair/sombre.",
            technologies = "Kotlin, Compose, Material 3"
        ),
        ProjectUi(
            id = 2,
            title = "Profil développeur",
            description = "Fiche de profil avec composants réutilisables, callbacks et previews.",
            technologies = "Kotlin, Compose"
        ),
        ProjectUi(
            id = 3,
            title = "Application de gestion",
            description = "Application web développée en alternance.",
            technologies = "Java, Angular, Git"
        )
    )
}

data class ProfileUiState(
    val projects: List<ProjectUi> = emptyList(),
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val favoriteProjectIds: List<Int> = emptyList()
)

class ProfileViewModel : ViewModel() {
    private val allProjects: List<ProjectUi> = sampleProjects()

    private val _uiState = MutableStateFlow(ProfileUiState(projects = allProjects))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private fun filterProjects(
        searchQuery: String,
        showOnlyFavorites: Boolean,
        favoriteProjectIds: List<Int>
    ): List<ProjectUi> {
        return allProjects.filter { project ->
            val matchesSearch =
                project.title.contains(searchQuery, ignoreCase = true)
                        || project.description.contains(searchQuery, ignoreCase = true)
                        || project.technologies.contains(searchQuery, ignoreCase = true)
            val matchesFavorite = !showOnlyFavorites || favoriteProjectIds.contains(project.id)
            matchesSearch && matchesFavorite
        }
    }

    fun onSearchQueryChange(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = newValue,
                projects = filterProjects(
                    searchQuery = newValue,
                    showOnlyFavorites = currentState.showOnlyFavorites,
                    favoriteProjectIds = currentState.favoriteProjectIds
                )
            )
        }
    }

    fun onToggleFavoriteFilter() {
        _uiState.update { currentState ->
            val newShowOnlyFavorites = !currentState.showOnlyFavorites

            currentState.copy(
                showOnlyFavorites = newShowOnlyFavorites,
                projects = filterProjects(
                    searchQuery = currentState.searchQuery,
                    showOnlyFavorites = newShowOnlyFavorites,
                    favoriteProjectIds = currentState.favoriteProjectIds
                )
            )
        }
    }

    fun onFavoriteClick(projectId: Int) {
        _uiState.update { currentState ->
            val newFavoriteIds =
                if (currentState.favoriteProjectIds.contains(projectId)) {
                    currentState.favoriteProjectIds - projectId
                } else {
                    currentState.favoriteProjectIds + projectId
                }

            currentState.copy(
                favoriteProjectIds = newFavoriteIds,
                projects = filterProjects(
                    searchQuery = currentState.searchQuery,
                    showOnlyFavorites = currentState.showOnlyFavorites,
                    favoriteProjectIds = newFavoriteIds
                )
            )
        }
    }
}

@Composable
fun ProfileContainer(
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        developer = sampleDeveloper(),
        skills = sampleSkills(),
        projects = uiState.projects,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        favoriteProjectIds = uiState.favoriteProjectIds,
        onFavoriteClick = viewModel::onFavoriteClick,
        onContactClick = onContactClick,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    developer: DeveloperUi,
    skills: List<SkillUi>,
    projects: List<ProjectUi>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    favoriteProjectIds: List<Int>,
    onFavoriteClick: (Int) -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeader(developer = developer)
        }
        item {
            SkillsSection(skills = skills)
        }
        item {
            Text(
                text = "Projets",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(text = "Rechercher un projet") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effacer la recherche"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (projects.isEmpty()) {
            item {
                Text(
                    text = "Aucun projet ne correspond à votre recherche.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(
                items = projects,
                key = { project -> project.id }
            ) { project ->
                ProjectCard(
                    project = project,
                    isFavorite = favoriteProjectIds.contains(project.id),
                    onFavoriteClick = { onFavoriteClick(project.id) }
                )
            }
        }
        item {
            ContactSection(developer = developer, onContactClick = onContactClick)
        }
    }
}

@Composable
fun AvatarWithBadge(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Avatar du développeur",
                modifier = Modifier.padding(20.dp)
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).size(20.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {}
    }
}

@Composable
fun ProfileHeader(developer: DeveloperUi, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithBadge()
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = developer.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = developer.role,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = developer.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SkillChip(skill: SkillUi, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Text(
            text = skill.name,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SkillsSection(skills: List<SkillUi>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Compétences",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = skills, key = { skill -> skill.id }) { skill ->
                SkillChip(skill = skill)
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectUi,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = project.technologies,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) {
                        "Retirer ${project.title} des favoris"
                    } else {
                        "Ajouter ${project.title} aux favoris"
                    },
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun ContactField(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(text = label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ContactButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text(text = "Contacter")
    }
}

@Composable
fun ContactSection(developer: DeveloperUi, onContactClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Contact",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            ContactField(
                label = "Email",
                value = developer.email,
                icon = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(8.dp))

            ContactField(
                label = "Localisation",
                value = developer.location,
                icon = Icons.Default.LocationOn
            )
            Spacer(modifier = Modifier.height(8.dp))

            ContactField(
                label = "Disponibilité",
                value = developer.availability,
                icon = Icons.Default.CheckCircle
            )
            Spacer(modifier = Modifier.height(16.dp))

            ContactButton(
                onClick = onContactClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Profil - Light Theme", heightDp = 1100)
@Composable
fun ProfileScreenLightPreview() {
    ProductExplorerTheme(darkTheme = false) {
        ProfileScreen(
            developer = sampleDeveloper(),
            skills = sampleSkills(),
            projects = sampleProjects(),
            searchQuery = "",
            onSearchQueryChange = {},
            favoriteProjectIds = emptyList(),
            onFavoriteClick = {},
            onContactClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Profil - Dark Theme", heightDp = 1100)
@Composable
fun ProfileScreenDarkPreview() {
    ProductExplorerTheme(darkTheme = true) {
        ProfileScreen(
            developer = sampleDeveloper(),
            skills = sampleSkills(),
            projects = sampleProjects(),
            searchQuery = "",
            onSearchQueryChange = {},
            favoriteProjectIds = emptyList(),
            onFavoriteClick = {},
            onContactClick = {}
        )
    }
}
