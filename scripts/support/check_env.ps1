# Check environment variables
Write-Host "Checking environment variables..." -ForegroundColor Cyan

$openaiKeyMissing = $false
$anthropicKeyMissing = $false

# Check OPENAI_API_KEY
if (-not $env:OPENAI_API_KEY) {
    Write-Host "OPENAI_API_KEY environment variable is not set" -ForegroundColor Yellow
    Write-Host "OpenAI models will not be available"
    Write-Host "Get an API key at https://platform.openai.com/api-keys"
    Write-Host "Please set it with: `$env:OPENAI_API_KEY='your_api_key'" -ForegroundColor Green
    $openaiKeyMissing = $true
} else {
    Write-Host "OPENAI_API_KEY set: OpenAI models are available" -ForegroundColor Green
}

# Check ANTHROPIC_API_KEY
if (-not $env:ANTHROPIC_API_KEY) {
    Write-Host "ANTHROPIC_API_KEY environment variable is not set" -ForegroundColor Yellow
    Write-Host "Claude models will not be available"
    Write-Host "Get an API key at https://www.anthropic.com/api"
    Write-Host "Please set it with: `$env:ANTHROPIC_API_KEY='your_api_key'" -ForegroundColor Green
    $anthropicKeyMissing = $true
} else {
    Write-Host "ANTHROPIC_API_KEY set: Claude models are available" -ForegroundColor Green
}

# Check if at least one API key is present
if ($openaiKeyMissing -and $anthropicKeyMissing) {
    Write-Host "ERROR: Both OPENAI_API_KEY and ANTHROPIC_API_KEY are missing." -ForegroundColor Red
    Write-Host "At least one API key is required to use language models." -ForegroundColor Red
    Write-Host "Please set at least one of these keys before running the application." -ForegroundColor Red
    exit 1
}

Write-Host "Environment check completed successfully." -ForegroundColor Green

exit 0  # Explicitly exit with success code